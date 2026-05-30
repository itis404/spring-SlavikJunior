package com.coffeeshop.service

import com.coffeeshop.contracts.OrderDetailDto
import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.contracts.OrderSummaryDto
import com.coffeeshop.contracts.PagedResponse
import com.coffeeshop.contracts.PaymentInitResponse
import com.coffeeshop.dto.AdminOrderDto
import com.coffeeshop.dto.BaristaQueueDto
import com.coffeeshop.dto.CreateOrderRequest

interface OrderService {
    fun createOrder(userId: Long, request: CreateOrderRequest): OrderSummaryDto
    fun getOrderHistory(userId: Long, page: Int = 0, size: Int = 20): PagedResponse<OrderSummaryDto>
    fun getActiveOrders(userId: Long): List<OrderSummaryDto>
    fun getOrderDetail(userId: Long, orderId: Long): OrderDetailDto
    fun cancelOrder(userId: Long, orderId: Long)
    fun initPayment(orderId: Long, userId: Long): PaymentInitResponse
    fun handlePaymentWebhook(tbankPaymentId: String, success: Boolean)

    // Admin
    fun updateStatus(orderId: Long, newStatus: OrderStatus)
    fun getPaidOrders(): List<OrderSummaryDto>
    fun getBaristaQueue(): BaristaQueueDto
    /** Returns order DTO for the admin panel. Throws EntityNotFoundException if not found. */
    fun getOrderForAdmin(orderId: Long): AdminOrderDto
    /** Verifies that the order belongs to the user. Throws ForbiddenException if not. */
    fun requireOrderOwnership(userId: Long, orderId: Long)
}
