package com.coffeeshop.service.impl

import com.coffeeshop.contracts.OrderDetailDto
import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.contracts.OrderSummaryDto
import com.coffeeshop.contracts.PagedResponse
import com.coffeeshop.contracts.PaymentInitResponse
import com.coffeeshop.contracts.PaymentStatus
import com.coffeeshop.dto.AdminOrderDto
import com.coffeeshop.dto.BaristaQueueDto
import com.coffeeshop.dto.CreateOrderRequest
import com.coffeeshop.dto.toBaristaDto
import com.coffeeshop.dto.toAdminOrderDto
import com.coffeeshop.dto.toDetailDto
import com.coffeeshop.dto.toSummaryDto
import com.coffeeshop.entity.Order
import com.coffeeshop.entity.OrderItem
import com.coffeeshop.entity.OrderItemModifier
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.exception.ForbiddenException
import com.coffeeshop.exception.InvalidOrderStateException
import com.coffeeshop.exception.OrderCancellationException
import com.coffeeshop.exception.ShopClosedException
import com.coffeeshop.repository.MenuItemRepository
import com.coffeeshop.repository.MenuItemVolumeRepository
import com.coffeeshop.repository.ModifierRepository
import com.coffeeshop.repository.OrderRepository
import com.coffeeshop.repository.UserRepository
import com.coffeeshop.service.FcmService
import com.coffeeshop.service.FileStorageService
import com.coffeeshop.service.OrderService
import com.coffeeshop.service.ShopSettingsService
import com.coffeeshop.service.TbankService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val menuItemRepository: MenuItemRepository,
    private val menuItemVolumeRepository: MenuItemVolumeRepository,
    private val modifierRepository: ModifierRepository,
    private val fcmService: FcmService,
    private val tbankService: TbankService,
    private val fileStorageService: FileStorageService,
    private val shopSettingsService: ShopSettingsService,
) : OrderService {

    override fun createOrder(userId: Long, request: CreateOrderRequest): OrderSummaryDto {
        val shopSettings = shopSettingsService.getStatus()
        if (!shopSettings.isAcceptingOrders) {
            throw ShopClosedException(shopSettings.closedMessage ?: "Магазин сейчас не принимает заказы")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User", userId) }

        val order = Order(
            user = user,
            totalPrice = BigDecimal.ZERO,
            comment = request.comment,
            receiptPhotoUrl = request.receiptPhotoUrl,
        )

        var totalPrice = BigDecimal.ZERO

        for (itemRequest in request.items) {
            val menuItem = menuItemRepository.findById(itemRequest.menuItemId)
                .orElseThrow { EntityNotFoundException("MenuItem", itemRequest.menuItemId) }

            if (!menuItem.isCurrentlyAvailable()) {
                throw EntityNotFoundException("MenuItem (available)", itemRequest.menuItemId)
            }

            val volume = menuItemVolumeRepository.findById(itemRequest.volumeId)
                .orElseThrow { EntityNotFoundException("MenuItemVolume", itemRequest.volumeId) }

            if (volume.menuItem.id != menuItem.id) {
                throw EntityNotFoundException("MenuItemVolume for item", itemRequest.volumeId)
            }

            // Load all modifiers in a single query instead of N individual findById calls
            val modifiers = if (itemRequest.modifierIds.isEmpty()) emptyList()
            else modifierRepository.findAllById(itemRequest.modifierIds)

            val orderItem = OrderItem(
                order = order,
                menuItem = menuItem,
                volume = volume,
                quantity = itemRequest.quantity,
                priceSnapshot = volume.price,
                comment = itemRequest.comment,
            )
            modifiers.forEach { modifier ->
                orderItem.modifiers.add(
                    OrderItemModifier(orderItem = orderItem, modifier = modifier, priceSnapshot = modifier.price),
                )
            }

            val itemTotal = volume.price
                .multiply(itemRequest.quantity.toBigDecimal())
                .add(modifiers.sumOf { it.price })

            totalPrice = totalPrice.add(itemTotal)
            order.items.add(orderItem)
        }

        order.totalPrice = totalPrice
        return orderRepository.save(order).toSummaryDto()
    }

    @Transactional(readOnly = true)
    override fun getOrderHistory(userId: Long, page: Int, size: Int): PagedResponse<OrderSummaryDto> {
        val paged: Page<Order> = orderRepository.findPagedByUserId(userId, PageRequest.of(page, size))
        val ids = paged.content.map { it.id }
        val withItems = if (ids.isEmpty()) emptyList() else orderRepository.findByIdsWithItems(ids)
        val byId = withItems.associateBy { it.id }
        return PagedResponse(
            content = paged.content.map { (byId[it.id] ?: it).toSummaryDto() },
            page = paged.number,
            size = paged.size,
            totalElements = paged.totalElements,
            totalPages = paged.totalPages,
            last = paged.isLast,
        )
    }

    @Transactional(readOnly = true)
    override fun getActiveOrders(userId: Long): List<OrderSummaryDto> =
        orderRepository.findActiveByUserIdWithItems(
            userId,
            listOf(OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.PREPARING, OrderStatus.READY),
        ).map { it.toSummaryDto() }

    @Transactional(readOnly = true)
    override fun getOrderDetail(userId: Long, orderId: Long): OrderDetailDto {
        val order = findOrderForUser(userId, orderId)
        return order.toDetailDto()
    }

    override fun cancelOrder(userId: Long, orderId: Long) {
        val order = findOrderForUser(userId, orderId)
        if (!order.canBeCancelledByClient()) {
            throw OrderCancellationException("Order ${order.id} cannot be cancelled in status ${order.orderStatus}")
        }
        order.orderStatus = OrderStatus.CANCELLED
        order.paymentStatus = if (order.paymentStatus == PaymentStatus.PAID) PaymentStatus.REFUND else PaymentStatus.CANCELLED
        clearReceiptPhoto(order)
        fcmService.sendOrderStatusUpdate(order.user.id, order.id, OrderStatus.CANCELLED)
    }

    override fun initPayment(orderId: Long, userId: Long): PaymentInitResponse {
        val order = findOrderForUser(userId, orderId)
        if (order.orderStatus != OrderStatus.PENDING) {
            throw InvalidOrderStateException(order.orderStatus, OrderStatus.PAID)
        }
        val response = tbankService.initPayment(order)
        order.tbankPaymentId = response.paymentId
        return response
    }

    override fun handlePaymentWebhook(tbankPaymentId: String, success: Boolean) {
        val order = orderRepository.findByTbankPaymentId(tbankPaymentId)
            ?: return // idempotent — ignore unknown payments
        if (success && order.orderStatus == OrderStatus.PENDING) {
            order.orderStatus = OrderStatus.PAID
            order.paymentStatus = PaymentStatus.PAID
            fcmService.sendOrderStatusUpdate(order.user.id, order.id, OrderStatus.PAID)
        }
    }

    override fun updateStatus(orderId: Long, newStatus: OrderStatus) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException("Order", orderId) }
        if (!Order.isValidStatusTransition(order.orderStatus, newStatus)) {
            throw InvalidOrderStateException(order.orderStatus, newStatus)
        }
        order.orderStatus = newStatus
        if (newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED) {
            clearReceiptPhoto(order)
        }
        fcmService.sendOrderStatusUpdate(order.user.id, order.id, newStatus)
    }

    @Transactional(readOnly = true)
    override fun getPaidOrders(): List<OrderSummaryDto> =
        orderRepository.findByOrderStatus(OrderStatus.PAID).map { it.toSummaryDto() }

    @Transactional(readOnly = true)
    override fun getBaristaQueue(): BaristaQueueDto = BaristaQueueDto(
        paid = orderRepository.findByOrderStatusWithDetails(OrderStatus.PAID).map { it.toBaristaDto() },
        preparing = orderRepository.findByOrderStatusWithDetails(OrderStatus.PREPARING).map { it.toBaristaDto() },
        ready = orderRepository.findByOrderStatusWithDetails(OrderStatus.READY).map { it.toBaristaDto() },
    )

    @Transactional(readOnly = true)
    override fun getOrderForAdmin(orderId: Long): AdminOrderDto =
        (orderRepository.findByIdForAdmin(orderId)
            ?: throw EntityNotFoundException("Order", orderId))
            .toAdminOrderDto()

    override fun requireOrderOwnership(userId: Long, orderId: Long) {
        findOrderForUser(userId, orderId)
    }

    private fun findOrderForUser(userId: Long, orderId: Long): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException("Order", orderId) }
        if (order.user.id != userId) throw ForbiddenException("Access denied")
        return order
    }

    private fun clearReceiptPhoto(order: Order) {
        val url = order.receiptPhotoUrl ?: return
        order.receiptPhotoUrl = null
        fileStorageService.delete(url)
    }
}
