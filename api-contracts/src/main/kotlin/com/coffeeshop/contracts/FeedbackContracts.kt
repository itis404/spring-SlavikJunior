package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class CreateFeedbackRequest(
    val text: String,
    val rating: Int,
)

@Serializable
data class FeedbackDto(
    val id: Long,
    val userName: String,
    val text: String,
    val rating: Int,
    /** ISO-8601 local datetime */
    val createdAt: String,
)
