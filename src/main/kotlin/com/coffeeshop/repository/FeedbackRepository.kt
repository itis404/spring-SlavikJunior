package com.coffeeshop.repository

import com.coffeeshop.entity.Feedback
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FeedbackRepository : JpaRepository<Feedback, Long> {

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Feedback>

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): List<Feedback>

    @Query("SELECT AVG(f.rating) FROM Feedback f")
    fun averageRating(): Double?
}
