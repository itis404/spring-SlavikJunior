package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class OrderCancellationException(message: String) :
    CoffeeShopException(message, HttpStatus.CONFLICT)
