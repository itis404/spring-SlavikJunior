package com.coffeeshop.dto

import jakarta.validation.constraints.Min

data class OrderItemRequest(
    val menuItemId: Long,
    val volumeId: Long,

    @field:Min(1)
    val quantity: Int = 1,

    val modifierIds: List<Long> = emptyList(),
    val comment: String? = null,
)
