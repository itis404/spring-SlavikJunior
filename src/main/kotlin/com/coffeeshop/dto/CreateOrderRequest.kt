package com.coffeeshop.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateOrderRequest(
    @field:NotEmpty
    @field:Valid
    val items: List<OrderItemRequest>,

    @field:Size(max = 500)
    val comment: String? = null,

    // Must start with /uploads/ (our known static files prefix) to prevent SSRF or
    // arbitrary external URL injection. Absolute URLs and path traversal are rejected.
    @field:Pattern(
        regexp = "^/uploads/[a-zA-Z0-9/_\\-\\.]+$",
        message = "receiptPhotoUrl must be a valid /uploads/ path",
    )
    val receiptPhotoUrl: String? = null,
)
