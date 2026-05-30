package com.coffeeshop.repository

import com.coffeeshop.entity.MenuItem

interface MenuItemRepositoryCustom {
    /** Поиск позиций меню по подстроке в названии (CriteriaBuilder, case-insensitive). */
    fun searchByName(keyword: String): List<MenuItem>
}
