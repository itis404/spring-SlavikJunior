package com.coffeeshop.controller.api

import com.coffeeshop.contracts.ProfileDto
import com.coffeeshop.dto.UpdateProfileRequest
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.annotation.AuthenticationPrincipal

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile")
@SecurityRequirement(name = "bearerAuth")
class ProfileController(private val userService: UserService) {

    @GetMapping
    @Operation(summary = "Get current user profile")
    fun getProfile(@AuthenticationPrincipal principal: UserPrincipal): ProfileDto =
        userService.getProfile(principal.userId)

    @PatchMapping
    @Operation(summary = "Update name or email")
    fun updateProfile(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UpdateProfileRequest,
    ): ProfileDto = userService.updateProfile(principal.userId, request)
}
