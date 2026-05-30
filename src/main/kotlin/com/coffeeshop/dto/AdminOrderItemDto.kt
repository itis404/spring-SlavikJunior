package com.coffeeshop.dto

data class AdminOrderItemDto(
    val menuItemName: String,
    val volumeMl: Int,
    val quantity: Int,
    val priceSnapshot: String,
    val modifiers: List<String>,
    val comment: String?,
)
