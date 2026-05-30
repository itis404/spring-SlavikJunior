package com.coffeeshop.service

import com.coffeeshop.contracts.PaymentInitResponse
import com.coffeeshop.entity.Order

interface TbankService {
    fun initPayment(order: Order): PaymentInitResponse

    /**
     * Verifies the webhook signature and processes the payment event.
     * Throws [com.coffeeshop.exception.ForbiddenException] if the signature is invalid.
     * Throws [com.coffeeshop.exception.PaymentException] if the payload is malformed.
     */
    fun processWebhook(payload: Map<String, Any>): WebhookResult
}
