package com.coffeeshop.domain

@JvmInline
value class Rating(val value: Int) {
    init {
        require(value in 1..5) { "Rating must be between 1 and 5, got $value" }
    }
}
