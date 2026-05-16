package com.coffeeshop.controller.api

import com.coffeeshop.contracts.ChatMessageDto
import com.coffeeshop.contracts.SendChatMessageRequest
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.ChatService
import com.coffeeshop.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders/{orderId}/chat")
@Tag(name = "Chat")
@SecurityRequirement(name = "bearerAuth")
class ChatController(
    private val chatService: ChatService,
    private val orderService: OrderService,
) {

    @GetMapping
    @Operation(summary = "Get chat history for an order")
    fun getHistory(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable orderId: Long,
    ): List<ChatMessageDto> {
        orderService.requireOrderOwnership(principal.userId, orderId)
        return chatService.getHistory(orderId)
    }

    @PostMapping
    @Operation(summary = "Send a message in order chat")
    fun sendMessage(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable orderId: Long,
        @RequestBody request: SendChatMessageRequest,
    ): ResponseEntity<ChatMessageDto> {
        orderService.requireOrderOwnership(principal.userId, orderId)
        val dto = chatService.sendMessage(orderId, principal.userId, request.text)
        return ResponseEntity.ok(dto)
    }
}
