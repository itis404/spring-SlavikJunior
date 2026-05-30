package com.coffeeshop.service.impl

import com.coffeeshop.contracts.ChatMessageDto
import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.dto.toDto
import com.coffeeshop.entity.ChatMessage
import com.coffeeshop.entity.Role
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.repository.ChatMessageRepository
import com.coffeeshop.repository.OrderRepository
import com.coffeeshop.repository.UserRepository
import com.coffeeshop.service.ChatService
import com.coffeeshop.service.FcmService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ChatServiceImpl(
    private val chatMessageRepository: ChatMessageRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val fcmService: FcmService,
) : ChatService {

    override fun sendMessage(orderId: Long, senderUserId: Long, text: String): ChatMessageDto {
        require(text.isNotBlank()) { "Сообщение не может быть пустым" }
        require(text.length <= 1000) { "Сообщение не должно превышать 1000 символов" }

        val order = orderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException("Order", orderId) }
        val sender = userRepository.findById(senderUserId)
            .orElseThrow { EntityNotFoundException("User", senderUserId) }

        val message = chatMessageRepository.save(ChatMessage(order = order, sender = sender, text = text))
        val dto = message.toDto()

        if (sender.role == Role.ADMIN) {
            fcmService.sendChatMessage(order.user.id, orderId, text)
        }

        return dto
    }

    @Transactional(readOnly = true)
    override fun getHistory(orderId: Long): List<ChatMessageDto> =
        chatMessageRepository.findByOrderIdOrderBySentAtAsc(orderId).map { it.toDto() }

    override fun cleanOldMessages(retentionDays: Long): Int =
        chatMessageRepository.deleteOldByOrderStatus(
            statuses = listOf(OrderStatus.COMPLETED, OrderStatus.CANCELLED),
            before = LocalDateTime.now().minusDays(retentionDays),
        )
}
