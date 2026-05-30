package com.coffeeshop.dto

import jakarta.validation.constraints.NotBlank

data class FirebaseVerifyRequest(
    @field:NotBlank
    val idToken: String,
)
