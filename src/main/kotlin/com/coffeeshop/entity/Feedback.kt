package com.coffeeshop.entity

import jakarta.persistence.*

@Entity
@Table(name = "feedback")
class Feedback(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, columnDefinition = "TEXT")
    val text: String,

    @Column(nullable = false)
    val rating: Int,
) : BaseEntity()
