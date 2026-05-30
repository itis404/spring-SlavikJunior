package com.coffeeshop.dto

import com.coffeeshop.contracts.OrderDetailDto
import com.coffeeshop.contracts.OrderItemDto
import com.coffeeshop.contracts.OrderItemModifierDto
import com.coffeeshop.contracts.OrderSummaryDto
import com.coffeeshop.entity.Order

fun Order.toBaristaDto() = BaristaOrderDto(
    id = id,
    userName = user.name,
    totalPrice = totalPrice,
    items = items.map { BaristaOrderItemDto(it.menuItem.name, it.quantity) },
    createdAt = createdAt,
)

fun Order.toSummaryDto() = OrderSummaryDto(
    id = id,
    orderStatus = orderStatus,
    paymentStatus = paymentStatus,
    totalPrice = totalPrice.toPlainString(),
    itemCount = items.sumOf { it.quantity },
    createdAt = createdAt.toString(),
)

fun Order.toDetailDto() = OrderDetailDto(
    id = id,
    orderStatus = orderStatus,
    paymentStatus = paymentStatus,
    totalPrice = totalPrice.toPlainString(),
    comment = comment,
    receiptPhotoUrl = receiptPhotoUrl,
    items = items.map { item ->
        OrderItemDto(
            id = item.id,
            menuItemName = item.menuItem.name,
            volumeMl = item.volume.volumeMl,
            quantity = item.quantity,
            priceSnapshot = item.priceSnapshot.toPlainString(),
            modifiers = item.modifiers.map { m ->
                OrderItemModifierDto(m.modifier.name, m.priceSnapshot.toPlainString())
            },
            comment = item.comment,
        )
    },
    createdAt = createdAt.toString(),
)
