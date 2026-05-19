package com.coffeeshop.service.impl

import com.coffeeshop.dto.AnalyticsDto
import com.coffeeshop.dto.PopularItemDto
import com.coffeeshop.dto.toSummaryDto
import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.repository.FeedbackRepository
import com.coffeeshop.repository.OrderRepository
import com.coffeeshop.service.AnalyticsService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class AnalyticsServiceImpl(
    private val orderRepository: OrderRepository,
    private val feedbackRepository: FeedbackRepository,
) : AnalyticsService {

    override fun getAnalytics(): AnalyticsDto {
        val popularRaw = orderRepository.findPopularItems(PageRequest.of(0, 10))
        val popularItems = popularRaw.map { row ->
            PopularItemDto(
                name = row[0] as String,
                orderCount = (row[1] as Number).toLong(),
                revenue = (row[2] as Number).let { BigDecimal(it.toString()) },
            )
        }

        val recentOrders = orderRepository.findTop20ByOrderStatusOrderByCreatedAtDesc(OrderStatus.COMPLETED)
            .map { it.toSummaryDto() }

        return AnalyticsDto(
            totalRevenue = orderRepository.sumRevenue(),
            totalOrders = orderRepository.countCompleted(),
            averageCheck = orderRepository.averageOrderValue(),
            averageRating = feedbackRepository.averageRating() ?: 0.0,
            popularItems = popularItems,
            recentOrders = recentOrders,
        )
    }
}
