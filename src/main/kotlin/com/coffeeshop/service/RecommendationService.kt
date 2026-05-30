package com.coffeeshop.service

import com.coffeeshop.contracts.MenuItemSummaryDto

interface RecommendationService {
    fun getPersonal(userId: Long): List<MenuItemSummaryDto>
    fun getFrequentlyBoughtTogether(itemIds: List<Long>): List<MenuItemSummaryDto>
}
