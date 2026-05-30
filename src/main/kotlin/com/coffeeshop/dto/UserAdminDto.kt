package com.coffeeshop.dto

import com.coffeeshop.entity.Role
import com.coffeeshop.entity.User
import java.time.LocalDateTime

data class UserAdminDto(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String?,
    val role: Role,
    val bonusPoints: Int,
    val createdAt: LocalDateTime,
)

fun User.toUserAdminDto() = UserAdminDto(
    id = id,
    name = name,
    phone = phone,
    email = email,
    role = role,
    bonusPoints = bonusPoints,
    createdAt = createdAt,
)
