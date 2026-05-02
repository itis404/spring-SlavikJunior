package com.coffeeshop.controller.api

import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuItemSummaryDto
import com.coffeeshop.contracts.MenuResponse
import com.coffeeshop.contracts.ModifierDto
import com.coffeeshop.service.MenuService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@Tag(name = "Menu", description = "Coffee shop menu — public endpoints")
class MenuController(private val menuService: MenuService) {

    @GetMapping("/menu")
    @Operation(summary = "Full menu grouped by category")
    fun getFullMenu(): MenuResponse = MenuResponse(menuService.getFullMenu())

    @GetMapping("/menu/{category}")
    @Operation(summary = "Items for a specific category")
    fun getByCategory(@PathVariable category: MenuCategory): List<MenuItemSummaryDto> =
        menuService.getByCategory(category)

    @GetMapping("/menu/items/{id}")
    @Operation(summary = "Item detail with all volumes and compatible modifiers")
    fun getItemDetail(@PathVariable id: Long): MenuItemDetailDto =
        menuService.getItemDetail(id)

    @GetMapping("/menu/search")
    @Operation(summary = "Search menu items by name (CriteriaBuilder, case-insensitive)")
    fun searchMenu(@RequestParam q: String): List<MenuItemSummaryDto> =
        menuService.searchItems(q)

    @GetMapping("/modifiers")
    @Operation(summary = "All available modifiers")
    fun getModifiers(): List<ModifierDto> = menuService.getAllModifiers()
}
