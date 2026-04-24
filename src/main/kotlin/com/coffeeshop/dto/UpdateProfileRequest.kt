package com.coffeeshop.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:Size(min = 1, max = 255)
    val name: String? = null,

    @field:Email
    val email: String? = null,
)
