package com.coffeeshop.controller.api

import com.coffeeshop.contracts.RegisterDeviceTokenRequest
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.DeviceTokenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/device-token")
@Tag(name = "Device Token")
@SecurityRequirement(name = "bearerAuth")
class DeviceTokenController(private val deviceTokenService: DeviceTokenService) {

    @PostMapping
    @Operation(summary = "Register or refresh FCM device token for the authenticated user")
    fun register(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: RegisterDeviceTokenRequest,
    ): ResponseEntity<Unit> {
        deviceTokenService.registerToken(principal.userId, request.fcmToken)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping
    @Operation(summary = "Remove FCM token on logout")
    fun remove(@AuthenticationPrincipal principal: UserPrincipal): ResponseEntity<Unit> {
        deviceTokenService.removeToken(principal.userId)
        return ResponseEntity.noContent().build()
    }
}
