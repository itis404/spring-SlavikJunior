package com.coffeeshop.repository

import com.coffeeshop.entity.DeviceToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface DeviceTokenRepository : JpaRepository<DeviceToken, Long> {

    fun findByUserId(userId: Long): DeviceToken?

    @Modifying
    @Query("DELETE FROM DeviceToken dt WHERE dt.user.id = :userId")
    fun deleteByUserId(userId: Long)
}
