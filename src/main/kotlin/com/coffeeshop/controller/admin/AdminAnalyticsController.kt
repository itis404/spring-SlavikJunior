package com.coffeeshop.controller.admin

import com.coffeeshop.service.AnalyticsService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/analytics")
class AdminAnalyticsController(private val analyticsService: AnalyticsService) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("analytics", analyticsService.getAnalytics())
        return "admin/analytics/index"
    }
}
