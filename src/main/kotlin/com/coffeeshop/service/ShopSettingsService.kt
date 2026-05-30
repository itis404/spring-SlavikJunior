package com.coffeeshop.service

import com.coffeeshop.entity.ShopSettings

interface ShopSettingsService {
    fun getStatus(): ShopSettings
    fun setAcceptingOrders(value: Boolean)
}
