package com.coffeeshop.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_item_modifiers")
class OrderItemModifier(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    val orderItem: OrderItem,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifier_id", nullable = false)
    val modifier: Modifier,

    // Price at the time of order — immutable snapshot
    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    val priceSnapshot: BigDecimal,
) : BaseEntity()
