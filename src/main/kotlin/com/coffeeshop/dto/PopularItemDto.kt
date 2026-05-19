package com.coffeeshop.dto

import java.math.BigDecimal

data class PopularItemDto(val name: String, val orderCount: Long, val revenue: BigDecimal)
