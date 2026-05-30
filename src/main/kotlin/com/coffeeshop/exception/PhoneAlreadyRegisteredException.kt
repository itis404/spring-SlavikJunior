package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class PhoneAlreadyRegisteredException :
    CoffeeShopException("Phone already registered", HttpStatus.CONFLICT)
