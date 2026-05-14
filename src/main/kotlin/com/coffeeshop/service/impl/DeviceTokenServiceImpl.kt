package com.coffeeshop.service.impl

import com.coffeeshop.entity.DeviceToken
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.repository.DeviceTokenRepository
import com.coffeeshop.repository.UserRepository
import com.coffeeshop.service.DeviceTokenService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeviceTokenServiceImpl(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val userRepository: UserRepository,
) : DeviceTokenService {

    private val log = LoggerFactory.getLogger(DeviceTokenServiceImpl::class.java)

    override fun registerToken(userId: Long, fcmToken: String) {
        require(fcmToken.isNotBlank()) { "FCM token must not be blank" }
        require(fcmToken.length <= 512) { "FCM token exceeds maximum length" }
        val existing = deviceTokenRepository.findByUserId(userId)
        if (existing != null) {
            existing.fcmToken = fcmToken
            log.debug("FCM token updated for user {}", userId)
        } else {
            val user = userRepository.findById(userId)
                .orElseThrow { EntityNotFoundException("User", userId) }
            deviceTokenRepository.save(DeviceToken(user = user, fcmToken = fcmToken))
            log.debug("FCM token registered for user {}", userId)
        }
    }

    @Transactional(readOnly = true)
    override fun getToken(userId: Long): String? =
        deviceTokenRepository.findByUserId(userId)?.fcmToken

    override fun removeToken(userId: Long) {
        deviceTokenRepository.deleteByUserId(userId)
        log.info("FCM token removed for user {}", userId)
    }
}
