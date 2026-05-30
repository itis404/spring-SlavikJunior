package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class UnauthorizedException(message: String = "Unauthorized") :
    CoffeeShopException(message, HttpStatus.UNAUTHORIZED)
