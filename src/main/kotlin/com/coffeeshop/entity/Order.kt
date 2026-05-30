package com.coffeeshop.entity

import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.contracts.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "orders")
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    var orderStatus: OrderStatus = OrderStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    var paymentStatus: PaymentStatus = PaymentStatus.UNPAID,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal,

    @Column(name = "tbank_payment_id")
    var tbankPaymentId: String? = null,

    @Column(name = "receipt_photo_url")
    var receiptPhotoUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    val comment: String? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<OrderItem> = mutableListOf(),

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val chatMessages: MutableList<ChatMessage> = mutableListOf(),
) : BaseEntity() {

    fun canBeCancelledByClient(): Boolean =
        orderStatus == OrderStatus.PENDING || orderStatus == OrderStatus.PAID

    companion object {
        /** Valid barista-driven transitions */
        fun isValidStatusTransition(from: OrderStatus, to: OrderStatus): Boolean =
            when (from) {
                OrderStatus.PAID -> to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED
                OrderStatus.PREPARING -> to == OrderStatus.READY || to == OrderStatus.CANCELLED
                OrderStatus.READY -> to == OrderStatus.COMPLETED
                else -> false
            }
    }
}
