package com.coffeeshop.domain

import java.math.BigDecimal

@JvmInline
value class Money(val amount: BigDecimal) {
    init {
        require(amount >= BigDecimal.ZERO) { "Amount cannot be negative" }
    }

    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun times(quantity: Int) = Money(amount * quantity.toBigDecimal())

    companion object {
        fun of(value: Number) = Money(BigDecimal(value.toString()))
        val ZERO = Money(BigDecimal.ZERO)
    }
}
