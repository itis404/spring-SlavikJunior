package com.coffeeshop.controller.admin

import com.coffeeshop.repository.UserRepository
import com.coffeeshop.service.FcmService
import com.coffeeshop.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/notifications")
class AdminNotificationController(
    private val fcmService: FcmService,
    private val userService: UserService,
    private val userRepository: UserRepository,
) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("users", userService.listAll())
        return "admin/notifications/index"
    }

    @PostMapping("/send")
    fun send(
        @RequestParam sendType: String,
        @RequestParam(required = false) phone: String?,
        @RequestParam(required = false) fcmToken: String?,
        @RequestParam title: String,
        @RequestParam body: String,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (title.isBlank() || body.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Заголовок и текст уведомления обязательны")
            return "redirect:/admin/notifications"
        }

        return when (sendType) {
            "BROADCAST" -> {
                val count = fcmService.broadcastCustom(title.trim(), body.trim())
                redirectAttributes.addFlashAttribute("success", "Уведомление отправлено $count устройствам")
                "redirect:/admin/notifications"
            }
            "BY_PHONE" -> {
                val trimmedPhone = phone?.trim().orEmpty()
                val user = userRepository.findByPhone(trimmedPhone)
                if (user == null) {
                    redirectAttributes.addFlashAttribute("error", "Пользователь с номером $trimmedPhone не найден")
                } else {
                    fcmService.sendCustomNotification(user.id, title.trim(), body.trim())
                    redirectAttributes.addFlashAttribute("success", "Уведомление отправлено пользователю ${user.name} ($trimmedPhone)")
                }
                "redirect:/admin/notifications"
            }
            "BY_TOKEN" -> {
                val token = fcmToken?.trim().orEmpty()
                if (token.isBlank()) {
                    redirectAttributes.addFlashAttribute("error", "FCM-токен не указан")
                } else {
                    fcmService.sendToToken(token, title.trim(), body.trim())
                    redirectAttributes.addFlashAttribute("success", "Уведомление отправлено на указанный токен")
                }
                "redirect:/admin/notifications"
            }
            else -> {
                redirectAttributes.addFlashAttribute("error", "Неизвестный тип отправки")
                "redirect:/admin/notifications"
            }
        }
    }
}
