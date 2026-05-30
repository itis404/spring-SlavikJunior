package com.coffeeshop.exception

import org.springframework.http.HttpStatus

class EntityNotFoundException(entity: String, id: Any) :
    CoffeeShopException("Resource not found", HttpStatus.NOT_FOUND) {
    val detail: String = "$entity not found: $id"
}
