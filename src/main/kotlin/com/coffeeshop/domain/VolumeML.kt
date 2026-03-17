package com.coffeeshop.domain

@JvmInline
value class VolumeML(val value: Int) {
    init {
        require(value > 0) { "Volume must be positive, got $value" }
    }
}
