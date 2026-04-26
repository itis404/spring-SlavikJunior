package com.coffeeshop.service

import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuItemSummaryDto
import com.coffeeshop.contracts.ModifierDto
import com.coffeeshop.dto.AdminMenuItemDto
import com.coffeeshop.form.MenuItemForm

interface MenuService {
    fun getFullMenu(): Map<String, List<MenuItemSummaryDto>>
    fun getByCategory(category: MenuCategory): List<MenuItemSummaryDto>
    fun getItemDetail(id: Long): MenuItemDetailDto
    fun getAllModifiers(): List<ModifierDto>
    fun searchItems(keyword: String): List<MenuItemSummaryDto>

    // Admin operations
    fun getAllItemsForAdmin(): List<AdminMenuItemDto>
    fun getItemForEdit(id: Long): AdminMenuItemDto
    fun createItem(form: MenuItemForm): AdminMenuItemDto
    fun updateItem(id: Long, form: MenuItemForm): AdminMenuItemDto
    fun toggleAvailability(id: Long): AdminMenuItemDto
    fun deleteItem(id: Long)
}
