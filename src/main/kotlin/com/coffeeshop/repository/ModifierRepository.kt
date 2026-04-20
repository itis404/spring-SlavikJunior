package com.coffeeshop.repository

import com.coffeeshop.entity.Modifier
import com.coffeeshop.contracts.ModifierCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ModifierRepository : JpaRepository<Modifier, Long> {

    fun findByIsAvailableTrue(): List<Modifier>

    fun findByCategoryAndIsAvailableTrue(category: ModifierCategory): List<Modifier>

    // University CriteriaBuilder-style query — find modifiers compatible with at least one menu item
    @Query(
        """
        SELECT DISTINCT mod FROM Modifier mod
        JOIN mod.menuItems mi
        WHERE mod.isAvailable = true
        AND mi.isHidden = false
        """,
    )
    fun findAvailableModifiersLinkedToVisibleItems(): List<Modifier>
}
