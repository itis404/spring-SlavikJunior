package com.coffeeshop.form

import com.coffeeshop.contracts.MenuCategory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MenuItemForm(
    @field:NotBlank
    val name: String = "",

    @field:NotNull
    val category: MenuCategory? = null,

    val description: String? = null,
    val photoUrl: String? = null,
    val isAvailable: Boolean = true,
    val isHidden: Boolean = false,

    // Comma-separated volumes, e.g. "250,350,450"
    val volumes: String = "",
    // Comma-separated prices aligned with volumes, e.g. "230,260,290"
    val prices: String = "",

    val modifierIds: List<Long> = emptyList(),
)
