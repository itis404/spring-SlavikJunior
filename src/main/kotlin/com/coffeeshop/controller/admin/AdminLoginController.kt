package com.coffeeshop.controller.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminLoginController {

    @GetMapping("/login")
    fun loginPage(
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?,
        model: org.springframework.ui.Model,
    ): String {
        if (error != null) model.addAttribute("error", "Неверный телефон или пароль")
        if (logout != null) model.addAttribute("logout", true)
        return "admin/login"
    }

    @GetMapping("")
    fun root() = "redirect:/admin/orders"
}
