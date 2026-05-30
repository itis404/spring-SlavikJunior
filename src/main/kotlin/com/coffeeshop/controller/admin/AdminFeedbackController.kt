package com.coffeeshop.controller.admin

import com.coffeeshop.service.FeedbackService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin/feedback")
class AdminFeedbackController(private val feedbackService: FeedbackService) {

    @GetMapping
    fun index(
        model: Model,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): String {
        model.addAttribute("feedbacks", feedbackService.getAll(page, size))
        model.addAttribute("page", page)
        return "admin/feedback/index"
    }
}
