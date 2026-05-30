package com.coffeeshop.contracts

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationResponse(val items: List<MenuItemSummaryDto>)
