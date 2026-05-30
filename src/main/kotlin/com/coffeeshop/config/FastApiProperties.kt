package com.coffeeshop.config

data class FastApiProperties(
    val url: String = "http://localhost:8001",
    val timeoutMs: Long = 3000,
)
