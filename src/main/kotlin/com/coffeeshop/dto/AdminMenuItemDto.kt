package com.coffeeshop.dto

import com.coffeeshop.contracts.MenuCategory
import java.time.LocalDateTime

data class AdminMenuItemDto(
    val id: Long,
    val name: String,
    val category: MenuCategory,
    val description: String?,
    val photoUrl: String?,
    val isAvailable: Boolean,
    val isHidden: Boolean,
    val isSeasonal: Boolean,
    val validFrom: LocalDateTime?,
    val validTo: LocalDateTime?,
    val volumeEntries: List<AdminVolumeEntry>,
    val modifierIds: List<Long> = emptyList(),
)
