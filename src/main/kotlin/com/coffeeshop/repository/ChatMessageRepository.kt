package com.coffeeshop.repository

import com.coffeeshop.contracts.OrderStatus
import com.coffeeshop.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {

    fun findByOrderIdOrderBySentAtAsc(orderId: Long): List<ChatMessage>

    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.order.orderStatus IN :statuses AND cm.sentAt < :before")
    fun deleteOldByOrderStatus(
        @Param("statuses") statuses: Collection<OrderStatus>,
        @Param("before") before: LocalDateTime,
    ): Int
}
