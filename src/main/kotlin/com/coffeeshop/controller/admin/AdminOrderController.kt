package com.coffeeshop.controller.admin

import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.ChatService
import com.coffeeshop.service.OrderService
import com.coffeeshop.service.ShopSettingsService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val orderService: OrderService,
    private val chatService: ChatService,
    private val shopSettingsService: ShopSettingsService,
) {

    @GetMapping
    fun index(model: Model): String {
        val queue = orderService.getBaristaQueue()
        model.addAttribute("paidOrders", queue.paid)
        model.addAttribute("preparingOrders", queue.preparing)
        model.addAttribute("readyOrders", queue.ready)
        model.addAttribute("shopIsOpen", shopSettingsService.getStatus().isAcceptingOrders)
        return "admin/orders/index"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("order", orderService.getOrderForAdmin(id))
        model.addAttribute("messages", chatService.getHistory(id))
        return "admin/orders/detail"
    }

    @PostMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam status: OrderStatus,
        redirectAttributes: RedirectAttributes,
    ): String {
        orderService.updateStatus(id, status)
        redirectAttributes.addFlashAttribute("success", "Статус заказа #$id изменён на ${status.name}")
        return "redirect:/admin/orders"
    }

    /** AJAX: получить историю чата (polling) */
    @GetMapping("/{id}/chat")
    @ResponseBody
    fun getChatHistory(@PathVariable id: Long): ResponseEntity<*> =
        ResponseEntity.ok(chatService.getHistory(id))

    /** AJAX: барыга отправляет сообщение */
    @PostMapping("/{id}/chat")
    @ResponseBody
    fun sendChatMessage(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam text: String,
    ): ResponseEntity<*> {
        val dto = chatService.sendMessage(id, principal.userId, text)
        return ResponseEntity.ok(dto)
    }
}
