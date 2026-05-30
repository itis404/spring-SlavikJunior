package com.coffeeshop.config

data class TbankProperties(
    val terminalKey: String = "",
    val secretKey: String = "",
    val apiUrl: String = "https://securepay.tinkoff.ru/v2",
)
