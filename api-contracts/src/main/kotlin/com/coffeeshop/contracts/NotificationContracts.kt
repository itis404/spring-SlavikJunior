package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceTokenRequest(
    val fcmToken: String,
)
