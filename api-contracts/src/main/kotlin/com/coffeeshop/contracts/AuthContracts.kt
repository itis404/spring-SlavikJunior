package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseVerifyRequest(val idToken: String)

@Serializable
data class FirebaseRegisterRequest(val idToken: String, val name: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
)
