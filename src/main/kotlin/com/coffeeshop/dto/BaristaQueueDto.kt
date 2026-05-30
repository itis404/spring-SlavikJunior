package com.coffeeshop.dto

data class BaristaQueueDto(
    val paid: List<BaristaOrderDto>,
    val preparing: List<BaristaOrderDto>,
    val ready: List<BaristaOrderDto>,
)
