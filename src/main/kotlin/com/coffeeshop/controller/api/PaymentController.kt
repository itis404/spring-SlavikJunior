package com.coffeeshop.controller.api

import com.coffeeshop.contracts.PaymentInitResponse
import com.coffeeshop.security.UserPrincipal
import com.coffeeshop.service.OrderService
import com.coffeeshop.service.TbankService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Payment")
class PaymentController(
    private val orderService: OrderService,
    private val tbankService: TbankService,
) {

    @PostMapping("/api/orders/{id}/payment/init")
    @Operation(summary = "Initialise Tbank payment — returns payment URL")
    @SecurityRequirement(name = "bearerAuth")
    fun initPayment(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ): ResponseEntity<PaymentInitResponse> {
        val response = orderService.initPayment(id, principal.userId)
        return ResponseEntity.ok(response)
    }

    /**
     * Tbank sends a signed webhook here after payment confirmation.
     * This endpoint must remain public (no JWT).
     * Signature verification and payload parsing are delegated to TbankService.
     */
    @PostMapping("/api/payment/webhook")
    @Operation(summary = "Tbank payment webhook (public)")
    fun handleWebhook(@RequestBody payload: Map<String, Any>): ResponseEntity<String> {
        val result = tbankService.processWebhook(payload)
        orderService.handlePaymentWebhook(result.paymentId, result.success)
        return ResponseEntity.ok("OK")
    }
}
