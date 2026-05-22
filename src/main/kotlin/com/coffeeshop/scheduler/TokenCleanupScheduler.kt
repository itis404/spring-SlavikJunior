package com.coffeeshop.scheduler

import com.coffeeshop.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TokenCleanupScheduler(private val authService: AuthService) {

    private val log = LoggerFactory.getLogger(TokenCleanupScheduler::class.java)

    /** Каждую ночь в 03:00 удаляет истёкшие и отозванные refresh-токены. */
    @Scheduled(cron = "0 0 3 * * *")
    fun cleanExpiredTokens() {
        val deleted = authService.cleanExpiredTokens()
        log.info("Token cleanup: удалено {} устаревших/отозванных refresh-токенов", deleted)
    }
}
