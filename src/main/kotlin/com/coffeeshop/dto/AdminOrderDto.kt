package com.coffeeshop.dto

import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.contracts.PaymentStatus
import com.coffeeshop.entity.Order
import java.math.BigDecimal
import java.time.LocalDateTime

data class AdminOrderDto(
    val id: Long,
    val orderStatus: OrderStatus,
    val paymentStatus: PaymentStatus,
    val totalPrice: BigDecimal,
    val comment: String?,
    val receiptPhotoUrl: String?,
    val userName: String,
    val userPhone: String,
    val tbankPaymentId: String?,
    val items: List<AdminOrderItemDto>,
    val createdAt: LocalDateTime,
)

fun Order.toAdminOrderDto() = AdminOrderDto(
    id = id,
    orderStatus = orderStatus,
    paymentStatus = paymentStatus,
    totalPrice = totalPrice,
    comment = comment,
    receiptPhotoUrl = receiptPhotoUrl,
    userName = user.name,
    userPhone = user.phone,
    tbankPaymentId = tbankPaymentId,
    items = items.map { item ->
        AdminOrderItemDto(
            menuItemName = item.menuItem.name,
            volumeMl = item.volume.volumeMl,
            quantity = item.quantity,
            priceSnapshot = item.priceSnapshot.toPlainString(),
            modifiers = item.modifiers.map { it.modifier.name },
            comment = item.comment,
        )
    },
    createdAt = createdAt,
)
