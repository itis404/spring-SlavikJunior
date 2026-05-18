package com.coffeeshop.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFeedbackRequest(
    @field:NotBlank
    @field:Size(max = 2000)
    val text: String,

    @field:Min(1)
    @field:Max(5)
    val rating: Int,
)
