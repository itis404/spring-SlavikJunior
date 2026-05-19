package com.coffeeshop.dto

import com.coffeeshop.contracts.OrderSummaryDto
import java.math.BigDecimal

data class AnalyticsDto(
    val totalRevenue: BigDecimal,
    val totalOrders: Long,
    val averageCheck: BigDecimal,
    val averageRating: Double,
    val popularItems: List<PopularItemDto>,
    val recentOrders: List<OrderSummaryDto>,
)
