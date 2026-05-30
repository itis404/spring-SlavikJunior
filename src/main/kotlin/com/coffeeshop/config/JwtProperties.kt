package com.coffeeshop.config

data class JwtProperties(
    val secret: String = "",
    val accessTokenExpiration: Long = 900_000L,
    val refreshTokenExpiration: Long = 2_592_000_000L,
)
