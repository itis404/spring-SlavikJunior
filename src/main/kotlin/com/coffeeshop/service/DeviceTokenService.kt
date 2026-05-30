package com.coffeeshop.service

interface DeviceTokenService {
    fun registerToken(userId: Long, fcmToken: String)
    fun getToken(userId: Long): String?
    fun removeToken(userId: Long)
}
