package com.coffeeshop.entity

import com.coffeeshop.contracts.MenuCategory
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "menu_items")
@SQLRestriction("deleted_at IS NULL")
class MenuItem(
    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    var category: MenuCategory,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "photo_url", length = 512)
    var photoUrl: String? = null,

    @Column(name = "is_available", nullable = false)
    var isAvailable: Boolean = true,

    // Hidden completely from menu (e.g. seasonal items off-season)
    @Column(name = "is_hidden", nullable = false)
    var isHidden: Boolean = false,

    @Column(name = "is_seasonal", nullable = false)
    var isSeasonal: Boolean = false,

    @Column(name = "valid_from")
    var validFrom: LocalDateTime? = null,

    @Column(name = "valid_to")
    var validTo: LocalDateTime? = null,

    @OneToMany(mappedBy = "menuItem", cascade = [CascadeType.ALL], orphanRemoval = true)
    val volumes: MutableList<MenuItemVolume> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "menu_item_modifiers",
        joinColumns = [JoinColumn(name = "menu_item_id")],
        inverseJoinColumns = [JoinColumn(name = "modifier_id")],
    )
    val compatibleModifiers: MutableList<Modifier> = mutableListOf(),
) : BaseEntity() {

    fun isCurrentlyAvailable(): Boolean {
        if (!isAvailable || isHidden) return false
        if (isSeasonal) {
            val now = LocalDateTime.now()
            if (validFrom != null && now.isBefore(validFrom)) return false
            if (validTo != null && now.isAfter(validTo)) return false
        }
        return true
    }
}
