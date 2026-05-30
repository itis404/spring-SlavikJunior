package com.coffeeshop.config

data class UploadProperties(
    val dir: String = "/app/uploads",
    val baseUrl: String = "/uploads",
    val maxFileSizeBytes: Long = 5 * 1024 * 1024,  // 5 MB
)
