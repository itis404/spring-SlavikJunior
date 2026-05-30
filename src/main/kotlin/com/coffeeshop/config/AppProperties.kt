package com.coffeeshop.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val currency: String = "RUB",
    val currencySymbol: String = "₽",
    val jwt: JwtProperties = JwtProperties(),
    val tbank: TbankProperties = TbankProperties(),
    val fastapi: FastApiProperties = FastApiProperties(),
    val uploads: UploadProperties = UploadProperties(),
    val firebase: FirebaseProperties = FirebaseProperties(),
    val cleanup: CleanupProperties = CleanupProperties(),
)
