package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class PaymentException(message: String) :
    CoffeeShopException(message, HttpStatus.BAD_GATEWAY)
