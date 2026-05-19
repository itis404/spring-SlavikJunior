package com.coffeeshop.controller.admin

import com.coffeeshop.exception.PhoneAlreadyRegisteredException
import com.coffeeshop.service.UserService
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/users")
class AdminUserController(private val userService: UserService) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("users", userService.listAll())
        return "admin/users/index"
    }

    @GetMapping("/new")
    fun newForm(): String = "admin/users/form"

    @PostMapping("/new")
    fun create(
        @RequestParam name: String,
        @RequestParam phone: String,
        @RequestParam(required = false) email: String?,
        @RequestParam(defaultValue = "0") bonusPoints: Int,
        redirectAttributes: RedirectAttributes,
    ): String {
        val trimmedPhone = phone.trim()
        if (!trimmedPhone.matches(Regex("^\\+[1-9]\\d{6,14}\$"))) {
            redirectAttributes.addFlashAttribute("error", "Неверный формат телефона. Используйте E.164, например +79161234567")
            return "redirect:/admin/users/new"
        }
        return try {
            userService.adminCreateUser(name.trim(), trimmedPhone, email?.takeIf { it.isNotBlank() }, bonusPoints)
            redirectAttributes.addFlashAttribute("success", "Пользователь ${name.trim()} успешно создан")
            "redirect:/admin/users"
        } catch (e: PhoneAlreadyRegisteredException) {
            redirectAttributes.addFlashAttribute("error", "Пользователь с номером $trimmedPhone уже существует")
            "redirect:/admin/users/new"
        }
    }
}
