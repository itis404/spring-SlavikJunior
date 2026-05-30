package com.coffeeshop.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class BaristaOrderDto(
    val id: Long,
    val userName: String,
    val totalPrice: BigDecimal,
    val items: List<BaristaOrderItemDto>,
    val createdAt: LocalDateTime,
)
