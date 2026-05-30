package com.coffeeshop.service

import com.coffeeshop.dto.AnalyticsDto

interface AnalyticsService {
    fun getAnalytics(): AnalyticsDto
}
