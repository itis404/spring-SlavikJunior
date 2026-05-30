package com.coffeeshop.repository

import com.coffeeshop.entity.MenuItemVolume
import org.springframework.data.jpa.repository.JpaRepository

interface MenuItemVolumeRepository : JpaRepository<MenuItemVolume, Long> {

    fun findByMenuItemId(menuItemId: Long): List<MenuItemVolume>
}
