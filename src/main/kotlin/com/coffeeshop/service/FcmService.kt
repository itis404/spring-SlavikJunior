package com.coffeeshop.service

import com.coffeeshop.contracts.OrderStatus

interface FcmService {
    fun sendOrderStatusUpdate(userId: Long, orderId: Long, status: OrderStatus)
    fun sendChatMessage(userId: Long, orderId: Long, text: String)
    fun sendCustomNotification(userId: Long, title: String, body: String)
    fun sendToToken(token: String, title: String, body: String)
    fun broadcastCustom(title: String, body: String): Int
}
