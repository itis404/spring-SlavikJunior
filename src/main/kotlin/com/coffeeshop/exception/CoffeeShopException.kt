package com.coffeeshop.exception

import org.springframework.http.HttpStatus

sealed class CoffeeShopException(
    message: String,
    val httpStatus: HttpStatus,
) : RuntimeException(message)
