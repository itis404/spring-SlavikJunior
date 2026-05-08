package com.coffeeshop.form

import com.coffeeshop.dto.AdminMenuItemDto

fun AdminMenuItemDto.toForm() = MenuItemForm(
    name = name,
    category = category,
    description = description,
    photoUrl = photoUrl,
    isAvailable = isAvailable,
    isHidden = isHidden,
    volumes = volumeEntries.joinToString(",") { it.volumeMl.toString() },
    prices = volumeEntries.joinToString(",") { it.price.toPlainString() },
    modifierIds = modifierIds,
)
