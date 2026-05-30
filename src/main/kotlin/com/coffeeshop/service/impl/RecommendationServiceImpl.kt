package com.coffeeshop.service.impl

import com.coffeeshop.config.AppProperties
import com.coffeeshop.contracts.MenuItemSummaryDto
import com.coffeeshop.dto.toSummaryDto
import com.coffeeshop.repository.MenuItemRepository
import com.coffeeshop.repository.OrderRepository
import com.coffeeshop.service.RecommendationService
import tools.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class RecommendationServiceImpl(
    private val appProperties: AppProperties,
    private val orderRepository: OrderRepository,
    private val menuItemRepository: MenuItemRepository,
    private val objectMapper: ObjectMapper,
) : RecommendationService {

    private val log = LoggerFactory.getLogger(RecommendationServiceImpl::class.java)
    private val json = "application/json; charset=utf-8".toMediaType()

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(appProperties.fastapi.timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(appProperties.fastapi.timeoutMs, TimeUnit.MILLISECONDS)
            .build()
    }

    override fun getPersonal(userId: Long): List<MenuItemSummaryDto> {
        val orderHistory = orderRepository.findByUserIdWithItemsAndMenuItemsOrderByCreatedAtDesc(userId)
            .flatMap { order -> order.items.map { it.menuItem.id } }
            .distinct()

        val payload = objectMapper.writeValueAsString(mapOf("userId" to userId, "itemIds" to orderHistory))
        return callFastApi("/recommendations/personal", payload)
    }

    override fun getFrequentlyBoughtTogether(itemIds: List<Long>): List<MenuItemSummaryDto> {
        val payload = objectMapper.writeValueAsString(mapOf("itemIds" to itemIds))
        return callFastApi("/recommendations/cart", payload)
    }

    private fun callFastApi(path: String, jsonBody: String): List<MenuItemSummaryDto> {
        return try {
            val request = Request.Builder()
                .url("${appProperties.fastapi.url}$path")
                .post(jsonBody.toRequestBody(json))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val body = response.body.string()

                @Suppress("UNCHECKED_CAST")
                val result = objectMapper.readValue(body, Map::class.java) as Map<String, Any>
                @Suppress("UNCHECKED_CAST")
                val ids = (result["itemIds"] as? List<Int>)?.map { it.toLong() } ?: return emptyList()

                menuItemRepository.findAllById(ids)
                    .filter { it.isCurrentlyAvailable() }
                    .map { it.toSummaryDto() }
            }
        } catch (e: Exception) {
            log.warn("FastAPI recommendation service unavailable: ${e.message}")
            emptyList() // graceful degradation
        }
    }
}
