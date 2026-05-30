package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class VolumeDto(
    val id: Long,
    val volumeMl: Int,
    val price: String,
)

@Serializable
data class ModifierDto(
    val id: Long,
    val name: String,
    val category: ModifierCategory,
    val price: String,
    val photoUrl: String? = null,
)

@Serializable
data class MenuItemSummaryDto(
    val id: Long,
    val name: String,
    val category: MenuCategory,
    val description: String?,
    val photoUrl: String?,
    val isAvailable: Boolean,
    val volumes: List<VolumeDto>,
)

@Serializable
data class MenuItemDetailDto(
    val id: Long,
    val name: String,
    val category: MenuCategory,
    val description: String?,
    val photoUrl: String?,
    val isAvailable: Boolean,
    val volumes: List<VolumeDto>,
    val compatibleModifiers: List<ModifierDto>,
)

@Serializable
data class MenuResponse(val categories: Map<String, List<MenuItemSummaryDto>>)
