package com.coffeeshop.converter

import com.coffeeshop.contracts.MenuCategory
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

/**
 * Converts a case-insensitive String to MenuCategory.
 * Used in MVC path variables and request parameters.
 * Required for ОРИС: custom converter.
 */
@Component
class StringToMenuCategoryConverter : Converter<String, MenuCategory> {
    override fun convert(source: String): MenuCategory =
        MenuCategory.valueOf(source.uppercase())
}
