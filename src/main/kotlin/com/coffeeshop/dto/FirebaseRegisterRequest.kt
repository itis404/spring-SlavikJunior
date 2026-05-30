package com.coffeeshop.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class FirebaseRegisterRequest(
    @field:NotBlank
    val idToken: String,
    @field:NotBlank
    @field:Size(min = 2, max = 100)
    val name: String,
)
