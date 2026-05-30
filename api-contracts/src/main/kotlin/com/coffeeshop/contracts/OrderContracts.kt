package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
    val comment: String? = null,
    val receiptPhotoUrl: String? = null,
)

@Serializable
data class OrderItemRequest(
    val menuItemId: Long,
    val volumeId: Long,
    val quantity: Int = 1,
    val modifierIds: List<Long> = emptyList(),
    val comment: String? = null,
)

@Serializable
data class OrderSummaryDto(
    val id: Long,
    val orderStatus: OrderStatus,
    val paymentStatus: PaymentStatus,
    /** BigDecimal serialized as plain string, e.g. "520.00" */
    val totalPrice: String,
    val itemCount: Int,
    /** ISO-8601 local datetime, e.g. "2026-03-26T10:30:00" */
    val createdAt: String,
)

@Serializable
data class OrderDetailDto(
    val id: Long,
    val orderStatus: OrderStatus,
    val paymentStatus: PaymentStatus,
    val totalPrice: String,
    val comment: String?,
    val receiptPhotoUrl: String? = null,
    val items: List<OrderItemDto>,
    val createdAt: String,
)

@Serializable
data class OrderItemDto(
    val id: Long,
    val menuItemName: String,
    val volumeMl: Int,
    val quantity: Int,
    val priceSnapshot: String,
    val modifiers: List<OrderItemModifierDto>,
    val comment: String?,
)

@Serializable
data class OrderItemModifierDto(
    val modifierName: String,
    val priceSnapshot: String,
)

@Serializable
data class PaymentInitResponse(
    val orderId: Long,
    val paymentUrl: String,
    val paymentId: String,
)

@Serializable
data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
)
