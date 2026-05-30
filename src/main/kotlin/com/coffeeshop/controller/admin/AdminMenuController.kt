package com.coffeeshop.controller.admin

import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.form.MenuItemForm
import com.coffeeshop.form.toForm
import com.coffeeshop.service.MenuService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/menu")
class AdminMenuController(private val menuService: MenuService) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("items", menuService.getAllItemsForAdmin())
        model.addAttribute("categories", MenuCategory.values())
        return "admin/menu/index"
    }

    @GetMapping("/new")
    fun newItemForm(model: Model): String {
        model.addAttribute("form", MenuItemForm())
        model.addAttribute("categories", MenuCategory.values())
        model.addAttribute("modifierGroups", modifierGroups())
        return "admin/menu/form"
    }

    @PostMapping("/new")
    fun createItem(
        @Valid @ModelAttribute("form") form: MenuItemForm,
        result: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (result.hasErrors()) {
            model.addAttribute("categories", MenuCategory.values())
            model.addAttribute("modifierGroups", modifierGroups())
            return "admin/menu/form"
        }
        val item = menuService.createItem(form)
        redirectAttributes.addFlashAttribute("success", "Позиция '${item.name}' добавлена")
        return "redirect:/admin/menu"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val item = menuService.getItemForEdit(id)
        model.addAttribute("form", item.toForm())
        model.addAttribute("item", item)
        model.addAttribute("categories", MenuCategory.values())
        model.addAttribute("modifierGroups", modifierGroups())
        return "admin/menu/form"
    }

    @PostMapping("/{id}/edit")
    fun updateItem(
        @PathVariable id: Long,
        @Valid @ModelAttribute("form") form: MenuItemForm,
        result: BindingResult,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (result.hasErrors()) {
            model.addAttribute("categories", MenuCategory.values())
            model.addAttribute("modifierGroups", modifierGroups())
            return "admin/menu/form"
        }
        val item = menuService.updateItem(id, form)
        redirectAttributes.addFlashAttribute("success", "Позиция '${item.name}' обновлена")
        return "redirect:/admin/menu"
    }

    private fun modifierGroups(): List<Map<String, Any>> =
        menuService.getAllModifiers()
            .groupBy { it.category.name }
            .map { (key, mods) ->
                mapOf(
                    "key" to key,
                    "modifiers" to mods.map { mapOf("id" to it.id, "name" to it.name, "price" to it.price) },
                )
            }

    /** MVC redirect endpoint — used by full-page form submit */
    @PostMapping("/{id}/toggle-availability")
    fun toggleAvailability(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val item = menuService.toggleAvailability(id)
        redirectAttributes.addFlashAttribute(
            "success",
            "'${item.name}' — ${if (item.isAvailable) "доступен" else "недоступен"}",
        )
        return "redirect:/admin/menu"
    }

    /** AJAX endpoint — returns JSON {success, isAvailable, message} */
    @PostMapping("/{id}/toggle-availability/ajax")
    @ResponseBody
    fun toggleAvailabilityAjax(@PathVariable id: Long): ResponseEntity<Map<String, Any>> {
        val item = menuService.toggleAvailability(id)
        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "isAvailable" to item.isAvailable,
                "message" to "'${item.name}' — ${if (item.isAvailable) "доступен" else "недоступен"}",
            ),
        )
    }

    @PostMapping("/{id}/delete")
    fun deleteItem(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        menuService.deleteItem(id)
        redirectAttributes.addFlashAttribute("success", "Позиция удалена")
        return "redirect:/admin/menu"
    }
}
