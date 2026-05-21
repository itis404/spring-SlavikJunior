package com.coffeeshop.controller.api

import com.coffeeshop.contracts.RecommendationResponse
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.RecommendationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations")
@SecurityRequirement(name = "bearerAuth")
class RecommendationController(private val recommendationService: RecommendationService) {

    @GetMapping("/personal")
    @Operation(summary = "Personalized recommendations based on order history (via FastAPI ML)")
    fun personal(@AuthenticationPrincipal principal: UserPrincipal): RecommendationResponse =
        RecommendationResponse(recommendationService.getPersonal(principal.userId))

    @GetMapping("/cart")
    @Operation(summary = "Frequently bought together — pass current cart item IDs")
    fun cart(@RequestParam itemIds: List<Long>): RecommendationResponse =
        RecommendationResponse(recommendationService.getFrequentlyBoughtTogether(itemIds))
}
