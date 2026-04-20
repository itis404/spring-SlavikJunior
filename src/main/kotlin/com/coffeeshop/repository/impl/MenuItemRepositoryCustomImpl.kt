package com.coffeeshop.repository.impl

import com.coffeeshop.entity.MenuItem
import com.coffeeshop.repository.MenuItemRepositoryCustom
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MenuItemRepositoryCustomImpl(
    private val em: EntityManager,
) : MenuItemRepositoryCustom {

    override fun searchByName(keyword: String): List<MenuItem> {
        val cb = em.criteriaBuilder
        val cq = cb.createQuery(MenuItem::class.java)
        val root = cq.from(MenuItem::class.java)

        cq.where(
            cb.and(
                cb.like(cb.lower(root.get("name")), "%${keyword.lowercase()}%"),
                cb.isNull(root.get<Any>("deletedAt")),
                cb.isFalse(root.get("isHidden")),
            ),
        )
        cq.orderBy(cb.asc(root.get<String>("name")))

        return em.createQuery(cq).resultList
    }
}
