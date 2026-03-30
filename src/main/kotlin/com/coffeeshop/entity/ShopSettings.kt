package com.coffeeshop.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shop_settings")
class ShopSettings(
    @Id
    val id: Long = 1L,
    var isAcceptingOrders: Boolean = true,
    var closedMessage: String? = null,
)
