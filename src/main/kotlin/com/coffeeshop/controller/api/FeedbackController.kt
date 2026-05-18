package com.coffeeshop.controller.api

import com.coffeeshop.contracts.FeedbackDto
import com.coffeeshop.dto.CreateFeedbackRequest
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.FeedbackService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feedback")
@Tag(name = "Feedback")
@SecurityRequirement(name = "bearerAuth")
class FeedbackController(private val feedbackService: FeedbackService) {

    @PostMapping
    @Operation(summary = "Submit feedback with rating (1–5)")
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateFeedbackRequest,
    ): ResponseEntity<FeedbackDto> {
        val dto = feedbackService.create(principal.userId, request)
        return ResponseEntity.ok(dto)
    }
}
