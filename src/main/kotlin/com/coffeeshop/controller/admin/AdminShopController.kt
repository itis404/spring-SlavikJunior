package com.coffeeshop.controller.admin

import com.coffeeshop.service.ShopSettingsService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AdminShopController(
    private val shopSettingsService: ShopSettingsService,
) {

    @PostMapping("/admin/shop/toggle")
    @ResponseBody
    fun toggle(): Map<String, Any> {
        val current = shopSettingsService.getStatus()
        val newValue = !current.isAcceptingOrders
        shopSettingsService.setAcceptingOrders(newValue)
        return mapOf("isOpen" to newValue)
    }
}
