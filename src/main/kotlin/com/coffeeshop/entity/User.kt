package com.coffeeshop.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
class User(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true, length = 20)
    val phone: String,

    @Column
    var email: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: Role = Role.CLIENT,

    @Column(name = "bonus_points", nullable = false)
    var bonusPoints: Int = 0,

    // Only set for ADMIN users — clients authenticate via Firebase Phone Auth
    @Column(name = "password_hash")
    var passwordHash: String? = null,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val orders: MutableList<Order> = mutableListOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val feedbacks: MutableList<Feedback> = mutableListOf(),
) : BaseEntity()
