package com.coffeeshop.repository

import com.coffeeshop.entity.Order
import com.coffeeshop.contracts.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDateTime

interface OrderRepository : JpaRepository<Order, Long> {

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Order>

    // Barista queue — fetches user + items + menuItem names eagerly to avoid N+1
    @Query(
        """
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.user
        JOIN FETCH o.items i
        JOIN FETCH i.menuItem
        WHERE o.orderStatus = :status
        ORDER BY o.createdAt ASC
        """,
    )
    fun findByOrderStatusWithDetails(@Param("status") status: OrderStatus): List<Order>

    // Order history — paginated without JOIN FETCH to avoid in-memory pagination
    @Query(
        value = "SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC",
        countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId",
    )
    fun findPagedByUserId(
        @Param("userId") userId: Long,
        pageable: Pageable,
    ): Page<Order>

    // Second-phase: eagerly load items for a known set of order IDs
    @Query(
        """
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.items
        WHERE o.id IN :ids
        """,
    )
    fun findByIdsWithItems(@Param("ids") ids: List<Long>): List<Order>

    // Recommendations: eagerly load items + menuItem for a user's full order history
    @Query(
        """
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.items i
        JOIN FETCH i.menuItem
        WHERE o.user.id = :userId
        ORDER BY o.createdAt DESC
        """,
    )
    fun findByUserIdWithItemsAndMenuItemsOrderByCreatedAtDesc(
        @Param("userId") userId: Long,
    ): List<Order>

    // Active orders — fetches items eagerly
    @Query(
        """
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.items
        WHERE o.user.id = :userId
        AND o.orderStatus IN :statuses
        """,
    )
    fun findActiveByUserIdWithItems(
        @Param("userId") userId: Long,
        @Param("statuses") statuses: List<OrderStatus>,
    ): List<Order>

    fun findByUserIdAndOrderStatusIn(userId: Long, statuses: List<OrderStatus>): List<Order>

    fun findByOrderStatusIn(statuses: List<OrderStatus>): List<Order>

    fun findByOrderStatus(status: OrderStatus): List<Order>

    // Admin order detail — eagerly loads all associations to avoid N+1
    @Query(
        """
        SELECT DISTINCT o FROM Order o
        JOIN FETCH o.user
        JOIN FETCH o.items i
        JOIN FETCH i.menuItem
        JOIN FETCH i.volume
        LEFT JOIN FETCH i.modifiers m
        LEFT JOIN FETCH m.modifier
        WHERE o.id = :id
        """,
    )
    fun findByIdForAdmin(@Param("id") id: Long): Order?

    // Analytics: last 20 completed orders — avoids loading all rows into memory
    fun findTop20ByOrderStatusOrderByCreatedAtDesc(status: OrderStatus): List<Order>

    // Analytics: total revenue for completed orders
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.orderStatus = 'COMPLETED'")
    fun sumRevenue(): BigDecimal

    // Analytics: average order value
    @Query("SELECT COALESCE(AVG(o.totalPrice), 0) FROM Order o WHERE o.orderStatus = 'COMPLETED'")
    fun averageOrderValue(): BigDecimal

    // Analytics: orders count for completed orders
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'COMPLETED'")
    fun countCompleted(): Long

    // Analytics: popular items (university — non-derived @Query)
    @Query(
        """
        SELECT oi.menuItem.name, SUM(oi.quantity), SUM(oi.priceSnapshot * oi.quantity)
        FROM OrderItem oi
        WHERE oi.order.orderStatus = 'COMPLETED'
        GROUP BY oi.menuItem.name
        ORDER BY SUM(oi.quantity) DESC
        """,
    )
    fun findPopularItems(pageable: Pageable): List<Array<Any>>

    // University subquery: orders from users who registered within the last 30 days
    @Query(
        """
        SELECT o FROM Order o
        WHERE o.user.id IN (
            SELECT u.id FROM User u
            WHERE u.createdAt > :since AND u.deletedAt IS NULL
        )
        ORDER BY o.createdAt DESC
        """,
    )
    fun findOrdersFromNewUsers(@Param("since") since: LocalDateTime): List<Order>

    fun findByTbankPaymentId(paymentId: String): Order?
}
