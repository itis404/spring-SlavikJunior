package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class ShopClosedException(message: String = "Магазин сейчас не принимает заказы") :
    CoffeeShopException(message, HttpStatus.SERVICE_UNAVAILABLE)
