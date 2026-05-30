package com.coffeeshop.service

import com.coffeeshop.contracts.ChatMessageDto

interface ChatService {
    /**
     * Validates and saves the message, returns DTO.
     * Authorization is verified by the caller before invoking this method.
     * Throws IllegalArgumentException if text is blank or exceeds 1000 characters.
     */
    fun sendMessage(orderId: Long, senderUserId: Long, text: String): ChatMessageDto

    /** История сообщений по заказу, от старых к новым. */
    fun getHistory(orderId: Long): List<ChatMessageDto>

    /** Удаляет сообщения завершённых/отменённых заказов старше [retentionDays] дней. */
    fun cleanOldMessages(retentionDays: Long): Int
}
