package com.coffeeshop.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    val menuItem: MenuItem,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volume_id", nullable = false)
    val volume: MenuItemVolume,

    @Column(nullable = false)
    val quantity: Int,

    // Price at the time of order — immutable snapshot
    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    val priceSnapshot: BigDecimal,

    @Column(columnDefinition = "TEXT")
    val comment: String? = null,

    @OneToMany(mappedBy = "orderItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    val modifiers: MutableList<OrderItemModifier> = mutableListOf(),
) : BaseEntity()
