package com.coffeeshop.dto

import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuItemSummaryDto
import com.coffeeshop.contracts.ModifierDto
import com.coffeeshop.contracts.VolumeDto
import com.coffeeshop.entity.MenuItem
import com.coffeeshop.entity.Modifier

fun MenuItem.toSummaryDto() = MenuItemSummaryDto(
    id = id,
    name = name,
    category = category,
    description = description,
    photoUrl = photoUrl,
    isAvailable = isCurrentlyAvailable(),
    volumes = volumes.map { VolumeDto(it.id, it.volumeMl, it.price.toPlainString()) },
)

fun MenuItem.toDetailDto() = MenuItemDetailDto(
    id = id,
    name = name,
    category = category,
    description = description,
    photoUrl = photoUrl,
    isAvailable = isCurrentlyAvailable(),
    volumes = volumes.map { VolumeDto(it.id, it.volumeMl, it.price.toPlainString()) },
    compatibleModifiers = compatibleModifiers.filter { it.isAvailable }.map { it.toDto() },
)

fun MenuItem.toAdminDto() = AdminMenuItemDto(
    id = id,
    name = name,
    category = category,
    description = description,
    photoUrl = photoUrl,
    isAvailable = isAvailable,
    isHidden = isHidden,
    isSeasonal = isSeasonal,
    validFrom = validFrom,
    validTo = validTo,
    volumeEntries = volumes.map { AdminVolumeEntry(it.volumeMl, it.price) },
)

fun MenuItem.toAdminDtoWithModifiers() = toAdminDto().copy(
    modifierIds = compatibleModifiers.map { it.id },
)

fun Modifier.toDto() = ModifierDto(id = id, name = name, category = category, price = price.toPlainString(), photoUrl = photoUrl)
