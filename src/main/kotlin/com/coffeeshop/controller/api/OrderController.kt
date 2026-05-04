package com.coffeeshop.controller.api

import com.coffeeshop.contracts.OrderDetailDto
import com.coffeeshop.contracts.OrderSummaryDto
import com.coffeeshop.contracts.PagedResponse
import com.coffeeshop.dto.CreateOrderRequest
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
@SecurityRequirement(name = "bearerAuth")
class OrderController(
    private val orderService: OrderService,
) {

    @PostMapping
    @Operation(summary = "Create a new order from client cart")
    fun createOrder(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: CreateOrderRequest,
    ): ResponseEntity<OrderSummaryDto> =
        ResponseEntity.ok(orderService.createOrder(principal.userId, request))

    @GetMapping
    @Operation(summary = "Order history (paginated)")
    fun getHistory(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): PagedResponse<OrderSummaryDto> =
        orderService.getOrderHistory(principal.userId, page, size)

    @GetMapping("/active")
    @Operation(summary = "Active orders (not completed or cancelled)")
    fun getActive(@AuthenticationPrincipal principal: UserPrincipal): List<OrderSummaryDto> =
        orderService.getActiveOrders(principal.userId)

    @GetMapping("/{id}")
    @Operation(summary = "Order detail")
    fun getDetail(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): OrderDetailDto = orderService.getOrderDetail(principal.userId, id)

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order (PENDING or PAID only)")
    fun cancel(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        orderService.cancelOrder(principal.userId, id)
        return ResponseEntity.ok().build()
    }

}
