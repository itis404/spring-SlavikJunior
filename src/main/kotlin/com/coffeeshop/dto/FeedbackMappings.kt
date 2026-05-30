package com.coffeeshop.dto

import com.coffeeshop.contracts.FeedbackDto
import com.coffeeshop.entity.Feedback

fun Feedback.toDto() = FeedbackDto(
    id = id,
    userName = user.name,
    text = text,
    rating = rating,
    createdAt = createdAt.toString(),
)
