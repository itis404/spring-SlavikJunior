package com.coffeeshop.service

import com.coffeeshop.contracts.FeedbackDto
import com.coffeeshop.dto.CreateFeedbackRequest

interface FeedbackService {
    fun create(userId: Long, request: CreateFeedbackRequest): FeedbackDto
    fun getAll(page: Int, size: Int): List<FeedbackDto>
    fun getByUser(userId: Long): List<FeedbackDto>
}
