package com.coffeeshop.service.impl

import com.coffeeshop.contracts.FeedbackDto
import com.coffeeshop.domain.Rating
import com.coffeeshop.dto.CreateFeedbackRequest
import com.coffeeshop.dto.toDto
import com.coffeeshop.entity.Feedback
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.repository.FeedbackRepository
import com.coffeeshop.repository.UserRepository
import com.coffeeshop.service.FeedbackService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedbackServiceImpl(
    private val feedbackRepository: FeedbackRepository,
    private val userRepository: UserRepository,
) : FeedbackService {

    override fun create(userId: Long, request: CreateFeedbackRequest): FeedbackDto {
        Rating(request.rating) // validates 1..5
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User", userId) }
        val feedback = feedbackRepository.save(
            Feedback(user = user, text = request.text, rating = request.rating),
        )
        return feedback.toDto()
    }

    @Transactional(readOnly = true)
    override fun getAll(page: Int, size: Int): List<FeedbackDto> =
        feedbackRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)).map { it.toDto() }

    @Transactional(readOnly = true)
    override fun getByUser(userId: Long): List<FeedbackDto> =
        feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId).map { it.toDto() }
}
