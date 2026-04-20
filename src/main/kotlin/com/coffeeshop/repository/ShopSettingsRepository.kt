package com.coffeeshop.repository

import com.coffeeshop.entity.ShopSettings
import org.springframework.data.jpa.repository.JpaRepository

interface ShopSettingsRepository : JpaRepository<ShopSettings, Long>
