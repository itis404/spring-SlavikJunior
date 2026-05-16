package com.coffeeshop.dto

import com.coffeeshop.contracts.ChatMessageDto
import com.coffeeshop.entity.ChatMessage

fun ChatMessage.toDto() = ChatMessageDto(
    id = id,
    senderRole = sender.role.name,
    text = text,
    sentAt = sentAt.toString(),
)
