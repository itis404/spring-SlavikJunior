package com.coffeeshop.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.firebase")
data class FirebaseProperties(
    val serviceAccountBase64: String = "",
)
