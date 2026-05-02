package com.coffeeshop.controller.api

import com.coffeeshop.contracts.TokenPair
import com.coffeeshop.dto.FirebaseRegisterRequest
import com.coffeeshop.dto.FirebaseVerifyRequest
import com.coffeeshop.dto.RefreshTokenRequest
import com.coffeeshop.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Firebase Phone authentication")
class AuthController(private val authService: AuthService) {

    @PostMapping("/firebase/verify")
    @Operation(summary = "Verify Firebase ID Token — login if user exists, 404 if new user")
    fun firebaseVerify(@Valid @RequestBody request: FirebaseVerifyRequest): ResponseEntity<TokenPair> {
        val tokens = authService.firebaseVerify(request.idToken)
        return ResponseEntity.ok(tokens)
    }

    @PostMapping("/firebase/register")
    @Operation(summary = "Register new user with Firebase ID Token + name, return JWT")
    fun firebaseRegister(@Valid @RequestBody request: FirebaseRegisterRequest): ResponseEntity<TokenPair> {
        val tokens = authService.firebaseRegister(request.idToken, request.name)
        return ResponseEntity.ok(tokens)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<TokenPair> {
        val tokens = authService.refresh(request.refreshToken)
        return ResponseEntity.ok(tokens)
    }
}
