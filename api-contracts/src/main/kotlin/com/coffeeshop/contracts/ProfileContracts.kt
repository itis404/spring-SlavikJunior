package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String?,
    val bonusPoints: Int,
    /** ISO-8601 local datetime */
    val registeredAt: String,
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
)
