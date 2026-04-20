package com.coffeeshop.repository

import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.entity.MenuItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MenuItemRepository : JpaRepository<MenuItem, Long>, MenuItemRepositoryCustom {

    fun findByCategoryAndIsHiddenFalse(category: MenuCategory): List<MenuItem>

    // Fetches volumes eagerly to avoid N+1 in per-category menu listing
    @Query(
        """
        SELECT DISTINCT m FROM MenuItem m
        LEFT JOIN FETCH m.volumes
        WHERE m.category = :category
        AND m.isHidden = false
        ORDER BY m.name
        """,
    )
    fun findByCategoryWithVolumes(@Param("category") category: MenuCategory): List<MenuItem>

    fun findByIsHiddenFalse(): List<MenuItem>

    // Fetch items with volumes eagerly to avoid N+1 in menu listing
    @Query(
        """
        SELECT DISTINCT m FROM MenuItem m
        LEFT JOIN FETCH m.volumes
        WHERE m.isHidden = false
        ORDER BY m.category, m.name
        """,
    )
    fun findAllVisibleWithVolumes(): List<MenuItem>

    @Query(
        """
        SELECT DISTINCT m FROM MenuItem m
        LEFT JOIN FETCH m.volumes
        ORDER BY m.category, m.name
        """,
    )
    fun findAllWithVolumesForAdmin(): List<MenuItem>

    // University subquery: items that have never been ordered
    @Query(
        """
        SELECT m FROM MenuItem m
        WHERE m.isHidden = false
        AND m.id NOT IN (
            SELECT oi.menuItem.id FROM OrderItem oi
        )
        """,
    )
    fun findNeverOrderedItems(): List<MenuItem>

    // Fetches volumes only. To also load compatibleModifiers, access them within the same
    // @Transactional context — Hibernate will fire a second query automatically.
    @Query(
        """
        SELECT DISTINCT m FROM MenuItem m
        LEFT JOIN FETCH m.volumes
        WHERE m.id = :id
        """,
    )
    fun findByIdWithVolumes(@Param("id") id: Long): MenuItem?
}
