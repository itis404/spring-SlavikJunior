package com.coffeeshop.service.impl

import com.coffeeshop.contracts.MenuCategory
import com.coffeeshop.contracts.MenuItemDetailDto
import com.coffeeshop.contracts.MenuItemSummaryDto
import com.coffeeshop.contracts.ModifierDto
import com.coffeeshop.dto.AdminMenuItemDto
import com.coffeeshop.dto.toAdminDto
import com.coffeeshop.dto.toAdminDtoWithModifiers
import com.coffeeshop.dto.toDetailDto
import com.coffeeshop.dto.toDto
import com.coffeeshop.dto.toSummaryDto
import com.coffeeshop.entity.MenuItem
import com.coffeeshop.entity.MenuItemVolume
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.form.MenuItemForm
import com.coffeeshop.repository.MenuItemRepository
import com.coffeeshop.repository.ModifierRepository
import com.coffeeshop.service.MenuService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class MenuServiceImpl(
    private val menuItemRepository: MenuItemRepository,
    private val modifierRepository: ModifierRepository,
) : MenuService {

    @Cacheable("menu")
    override fun getFullMenu(): Map<String, List<MenuItemSummaryDto>> =
        menuItemRepository.findAllVisibleWithVolumes()
            .map { it.toSummaryDto() }
            .groupBy { it.category.name }

    override fun getByCategory(category: MenuCategory): List<MenuItemSummaryDto> =
        menuItemRepository.findByCategoryWithVolumes(category)
            .map { it.toSummaryDto() }

    override fun getItemDetail(id: Long): MenuItemDetailDto {
        val item = menuItemRepository.findByIdWithVolumes(id)
            ?: throw EntityNotFoundException("MenuItem", id)
        item.compatibleModifiers.size // trigger lazy load within transaction
        return item.toDetailDto()
    }

    @Cacheable("modifiers")
    override fun getAllModifiers(): List<ModifierDto> =
        modifierRepository.findByIsAvailableTrue().map { it.toDto() }

    override fun getAllItemsForAdmin(): List<AdminMenuItemDto> =
        menuItemRepository.findAllWithVolumesForAdmin().map { it.toAdminDto() }

    override fun getItemForEdit(id: Long): AdminMenuItemDto {
        val item = menuItemRepository.findByIdWithVolumes(id)
            ?: throw EntityNotFoundException("MenuItem", id)
        item.compatibleModifiers.size // trigger lazy load within transaction
        return item.toAdminDtoWithModifiers()
    }

    @Transactional
    @CacheEvict(cacheNames = ["menu", "modifiers"], allEntries = true)
    override fun createItem(form: MenuItemForm): AdminMenuItemDto {
        val item = menuItemRepository.save(
            MenuItem(
                name = form.name,
                category = form.category!!,
                description = form.description,
                photoUrl = form.photoUrl,
                isAvailable = form.isAvailable,
                isHidden = form.isHidden,
            ),
        )
        parseVolumesAndPrices(form.volumes, form.prices).forEach { (ml, price) ->
            item.volumes.add(MenuItemVolume(menuItem = item, volumeMl = ml, price = price))
        }
        if (form.modifierIds.isNotEmpty()) {
            item.compatibleModifiers.addAll(modifierRepository.findAllById(form.modifierIds))
        }
        return item.toAdminDto()
    }

    @Transactional
    @CacheEvict(cacheNames = ["menu", "modifiers"], allEntries = true)
    override fun updateItem(id: Long, form: MenuItemForm): AdminMenuItemDto {
        val item = menuItemRepository.findById(id)
            .orElseThrow { EntityNotFoundException("MenuItem", id) }
        item.name = form.name
        item.category = form.category!!
        item.description = form.description
        item.photoUrl = form.photoUrl
        item.isAvailable = form.isAvailable
        item.isHidden = form.isHidden
        item.volumes.clear()
        parseVolumesAndPrices(form.volumes, form.prices).forEach { (ml, price) ->
            item.volumes.add(MenuItemVolume(menuItem = item, volumeMl = ml, price = price))
        }
        item.compatibleModifiers.clear()
        if (form.modifierIds.isNotEmpty()) {
            item.compatibleModifiers.addAll(modifierRepository.findAllById(form.modifierIds))
        }
        return item.toAdminDto()
    }

    @Transactional
    @CacheEvict(cacheNames = ["menu"], allEntries = true)
    override fun toggleAvailability(id: Long): AdminMenuItemDto {
        val item = menuItemRepository.findById(id)
            .orElseThrow { EntityNotFoundException("MenuItem", id) }
        item.isAvailable = !item.isAvailable
        return item.toAdminDto()
    }

    @Transactional
    @CacheEvict(cacheNames = ["menu"], allEntries = true)
    override fun deleteItem(id: Long) {
        val item = menuItemRepository.findById(id)
            .orElseThrow { EntityNotFoundException("MenuItem", id) }
        item.softDelete()
        item.isHidden = true
    }

    override fun searchItems(keyword: String): List<MenuItemSummaryDto> =
        menuItemRepository.searchByName(keyword).map { it.toSummaryDto() }

    private fun parseVolumesAndPrices(volumesStr: String, pricesStr: String): List<Pair<Int, BigDecimal>> {
        val vols = volumesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val prs = pricesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return try {
            vols.zip(prs).map { (v, p) -> v.toInt() to BigDecimal(p) }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Неверный формат объёмов или цен: ${e.message}")
        }
    }
}
