package com.coffeeshop.controller.api

import com.coffeeshop.contracts.ShopStatusResponse
import com.coffeeshop.service.ShopSettingsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/shop")
class ShopController(
    private val shopSettingsService: ShopSettingsService,
) {

    @GetMapping("/status")
    fun status(): ShopStatusResponse {
        val settings = shopSettingsService.getStatus()
        return ShopStatusResponse(
            isOpen = settings.isAcceptingOrders,
            message = settings.closedMessage,
        )
    }
}
