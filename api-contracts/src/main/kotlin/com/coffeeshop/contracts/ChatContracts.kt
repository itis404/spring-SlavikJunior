package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: Long,
    /** "CLIENT" or "ADMIN" */
    val senderRole: String,
    val text: String,
    /** ISO-8601 local datetime, e.g. "2026-03-27T10:30:00" */
    val sentAt: String,
)

@Serializable
data class SendChatMessageRequest(val text: String)
