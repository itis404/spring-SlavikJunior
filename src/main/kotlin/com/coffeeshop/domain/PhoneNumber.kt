package com.coffeeshop.domain

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(value.matches(Regex("^\\+[1-9]\\d{6,14}$"))) {
            "Phone must be in E.164 format (e.g. +79001234567)"
        }
    }
}
