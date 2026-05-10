package com.coffeeshop.converter

import com.coffeeshop.contracts.OrderStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

/**
 * Converts a case-insensitive String to OrderStatus.
 * Used in MVC request parameters (e.g., @RequestParam status: OrderStatus).
 */
@Component
class StringToOrderStatusConverter : Converter<String, OrderStatus> {
    override fun convert(source: String): OrderStatus =
        OrderStatus.valueOf(source.uppercase())
}
