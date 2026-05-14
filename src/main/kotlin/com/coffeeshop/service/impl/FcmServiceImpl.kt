package com.coffeeshop.service.impl

import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.repository.DeviceTokenRepository
import com.coffeeshop.service.DeviceTokenService
import com.coffeeshop.service.FcmService
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FcmServiceImpl(
    private val firebaseApp: FirebaseApp?,
    private val deviceTokenService: DeviceTokenService,
    private val deviceTokenRepository: DeviceTokenRepository,
) : FcmService {
    private val log = LoggerFactory.getLogger(FcmServiceImpl::class.java)

    override fun sendOrderStatusUpdate(userId: Long, orderId: Long, status: OrderStatus) {
        val (title, body) = orderStatusMessage(status)
        sendToUser(
            userId = userId,
            title = title,
            body = body,
            data = mapOf(
                "type"    to "ORDER_STATUS",
                "orderId" to orderId.toString(),
                "status"  to status.name,
            ),
        )
    }

    override fun sendChatMessage(userId: Long, orderId: Long, text: String) {
        sendToUser(
            userId = userId,
            title = "Новое сообщение от бариста",
            body = text.take(100),
            data = mapOf(
                "type"    to "CHAT_MESSAGE",
                "orderId" to orderId.toString(),
            ),
        )
    }

    override fun sendCustomNotification(userId: Long, title: String, body: String) {
        sendToUser(
            userId = userId,
            title = title,
            body = body,
            data = mapOf("type" to "CUSTOM"),
        )
    }

    override fun sendToToken(token: String, title: String, body: String) {
        if (firebaseApp == null) {
            log.debug("FCM disabled — skipping push to token")
            return
        }
        val message = buildMessage(token, title, body, mapOf("type" to "CUSTOM"))
        try {
            val messageId = FirebaseMessaging.getInstance(firebaseApp).send(message)
            log.debug("FCM sent to token — messageId={}", messageId)
        } catch (e: FirebaseMessagingException) {
            log.error("FCM error sending to token — code={}", e.messagingErrorCode, e)
        } catch (e: Exception) {
            log.error("FCM unexpected error sending to token", e)
        }
    }

    override fun broadcastCustom(title: String, body: String): Int {
        if (firebaseApp == null) {
            log.debug("FCM disabled — skipping broadcast")
            return 0
        }
        val tokens = deviceTokenRepository.findAll().map { it.fcmToken }
        if (tokens.isEmpty()) return 0

        var sent = 0
        tokens.forEach { token ->
            val message = buildMessage(token, title, body, mapOf("type" to "CUSTOM"))
            try {
                FirebaseMessaging.getInstance(firebaseApp).send(message)
                sent++
            } catch (e: FirebaseMessagingException) {
                log.warn("FCM broadcast: error for token, code={}", e.messagingErrorCode)
            } catch (e: Exception) {
                log.error("FCM broadcast: unexpected error", e)
            }
        }
        log.info("FCM broadcast sent to {}/{} devices", sent, tokens.size)
        return sent
    }

    private fun sendToUser(userId: Long, title: String, body: String, data: Map<String, String>) {
        if (firebaseApp == null) {
            log.debug("FCM disabled — skipping push to user {}", userId)
            return
        }
        val token = deviceTokenService.getToken(userId)
        if (token == null) {
            log.debug("No FCM token for user {} — skipping push", userId)
            return
        }

        val message = buildMessage(token, title, body, data)
        try {
            val messageId = FirebaseMessaging.getInstance(firebaseApp).send(message)
            log.debug("FCM sent to user {} — messageId={}", userId, messageId)
        } catch (e: FirebaseMessagingException) {
            handleFirebaseError(e, userId)
        } catch (e: Exception) {
            log.error("FCM unexpected error sending to user {}", userId, e)
        }
    }

    private fun buildMessage(token: String, title: String, body: String, data: Map<String, String>): Message =
        Message.builder()
            .setToken(token)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build(),
            )
            .putAllData(data)
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(
                        AndroidNotification.builder()
                            .setChannelId("order_updates")
                            .build(),
                    )
                    .build(),
            )
            .build()

    private fun handleFirebaseError(e: FirebaseMessagingException, userId: Long) {
        when (e.messagingErrorCode) {
            MessagingErrorCode.UNREGISTERED,
            MessagingErrorCode.INVALID_ARGUMENT -> {
                log.warn("FCM token for user {} is invalid ({}), removing", userId, e.messagingErrorCode)
                deviceTokenService.removeToken(userId)
            }
            MessagingErrorCode.QUOTA_EXCEEDED ->
                log.warn("FCM quota exceeded for user {} — message lost", userId)
            else ->
                log.error("FCM error for user {} — code={}", userId, e.messagingErrorCode, e)
        }
    }

    private fun orderStatusMessage(status: OrderStatus): Pair<String, String> = when (status) {
        OrderStatus.PENDING   -> "Заказ создан"     to "Ожидаем оплату"
        OrderStatus.PAID      -> "Оплата принята"   to "Ваш заказ подтверждён, мы уже готовим его"
        OrderStatus.PREPARING -> "Заказ готовится"  to "Бариста приступил к приготовлению"
        OrderStatus.READY     -> "Заказ готов!"     to "Подойдите к стойке, ваш заказ ждёт"
        OrderStatus.COMPLETED -> "Заказ выдан"      to "Приятного! Будем рады видеть снова"
        OrderStatus.CANCELLED -> "Заказ отменён"    to "Ваш заказ был отменён"
    }
}
