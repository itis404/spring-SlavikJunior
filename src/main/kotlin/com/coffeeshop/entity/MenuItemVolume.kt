package com.coffeeshop.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "menu_item_volumes")
class MenuItemVolume(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    val menuItem: MenuItem,

    @Column(name = "volume_ml", nullable = false)
    val volumeMl: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,
) : BaseEntity()
