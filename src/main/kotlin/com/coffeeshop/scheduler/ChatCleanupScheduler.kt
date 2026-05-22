package com.coffeeshop.scheduler

import com.coffeeshop.config.AppProperties
import com.coffeeshop.service.ChatService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ChatCleanupScheduler(
    private val chatService: ChatService,
    private val appProperties: AppProperties,
) {
    private val log = LoggerFactory.getLogger(ChatCleanupScheduler::class.java)

    /** Каждую ночь в 03:15 удаляет сообщения завершённых/отменённых заказов старше app.cleanup.chat-retention-days дней. */
    @Scheduled(cron = "0 15 3 * * *")
    fun cleanOldMessages() {
        val deleted = chatService.cleanOldMessages(appProperties.cleanup.chatRetentionDays)
        log.info("Chat cleanup: удалено {} старых сообщений (retention={}d)", deleted, appProperties.cleanup.chatRetentionDays)
    }
}
