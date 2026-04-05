package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
enum class MenuCategory { COFFEE, MATCHA, NON_COFFEE, SIGNATURE }

@Serializable
enum class ModifierCategory { SYRUP, MARSHMALLOW, ALT_MILK, VITAMIN_SHOT }

@Serializable
enum class OrderStatus { PENDING, PAID, PREPARING, READY, COMPLETED, CANCELLED }

@Serializable
enum class PaymentStatus { UNPAID, PAID, REFUND, CANCELLED }
