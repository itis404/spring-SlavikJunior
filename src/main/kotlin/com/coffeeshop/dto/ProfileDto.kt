package com.coffeeshop.dto

import com.coffeeshop.contracts.ProfileDto
import com.coffeeshop.entity.User

fun User.toProfileDto() = ProfileDto(
    id = id,
    name = name,
    phone = phone,
    email = email,
    bonusPoints = bonusPoints,
    registeredAt = createdAt.toString(),
)
