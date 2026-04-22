package com.coffeeshop.exception

import com.coffeeshop.contracts.OrderStatus
import org.springframework.http.HttpStatus

class InvalidOrderStateException(from: OrderStatus, to: OrderStatus) :
    CoffeeShopException("Cannot transition order status $from → $to", HttpStatus.CONFLICT)
