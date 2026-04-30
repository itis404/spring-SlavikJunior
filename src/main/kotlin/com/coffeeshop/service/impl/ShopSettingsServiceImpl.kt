package com.coffeeshop.service.impl

import com.coffeeshop.entity.ShopSettings
import com.coffeeshop.repository.ShopSettingsRepository
import com.coffeeshop.service.ShopSettingsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShopSettingsServiceImpl(
    private val repo: ShopSettingsRepository,
) : ShopSettingsService {

    private fun findOrCreate(): ShopSettings =
        repo.findById(1L).orElseGet { repo.save(ShopSettings(id = 1L)) }

    @Transactional(readOnly = true)
    override fun getStatus(): ShopSettings = findOrCreate()

    override fun setAcceptingOrders(value: Boolean) {
        val settings = findOrCreate()
        settings.isAcceptingOrders = value
        repo.save(settings)
    }
}
