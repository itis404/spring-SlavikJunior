package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class ShopStatusResponse(
    val isOpen: Boolean,
    val message: String? = null,
)
