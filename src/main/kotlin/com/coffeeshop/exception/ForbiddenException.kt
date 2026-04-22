package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class ForbiddenException(message: String = "Access denied") :
    CoffeeShopException(message, HttpStatus.FORBIDDEN)
