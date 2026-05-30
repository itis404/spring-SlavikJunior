package com.coffeeshop.entity

import com.coffeeshop.contracts.ModifierCategory
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "modifiers")
class Modifier(
    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val category: ModifierCategory,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(name = "is_available", nullable = false)
    var isAvailable: Boolean = true,

    @Column(name = "photo_url", length = 512)
    var photoUrl: String? = null,

    @ManyToMany(mappedBy = "compatibleModifiers")
    val menuItems: MutableList<MenuItem> = mutableListOf(),
) : BaseEntity()
