package com.coffeeshop.service.impl

import com.coffeeshop.config.AppProperties
import com.coffeeshop.contracts.PaymentInitResponse
import com.coffeeshop.entity.Order
import com.coffeeshop.exception.ForbiddenException
import com.coffeeshop.exception.PaymentException
import com.coffeeshop.service.TbankService
import com.coffeeshop.service.WebhookResult
import tools.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.security.MessageDigest

@Service
class TbankServiceImpl(
    private val appProperties: AppProperties,
    private val objectMapper: ObjectMapper,
) : TbankService {

    private val log = LoggerFactory.getLogger(TbankServiceImpl::class.java)
    private val client = OkHttpClient()
    private val json = "application/json; charset=utf-8".toMediaType()

    override fun initPayment(order: Order): PaymentInitResponse {
        val terminalKey = appProperties.tbank.terminalKey
        if (terminalKey.isBlank()) {
            // Dev mode — return a stub payment URL
            log.info("Tbank [DEV] payment init for order ${order.id}")
            val fakePaymentId = "dev-${order.id}"
            return PaymentInitResponse(
                orderId = order.id,
                paymentUrl = "https://securepay.tinkoff.ru/stub/${order.id}",
                paymentId = fakePaymentId,
            )
        }

        val amountKopeks = order.totalPrice.multiply(BigDecimal("100")).toLong()
        val orderId = order.id.toString()
        val payload = mapOf(
            "TerminalKey" to terminalKey,
            "Amount" to amountKopeks,
            "OrderId" to orderId,
            "Description" to "Заказ #$orderId",
            "Token" to buildToken(terminalKey, amountKopeks, orderId),
        )

        val body = objectMapper.writeValueAsString(payload).toRequestBody(json)
        val request = Request.Builder()
            .url("${appProperties.tbank.apiUrl}/Init")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body.string()

            if (!response.isSuccessful) {
                throw PaymentException("Tbank error ${response.code}: $responseBody")
            }

            @Suppress("UNCHECKED_CAST")
            val result = objectMapper.readValue(responseBody, Map::class.java) as Map<String, Any>
            val success = result["Success"] as? Boolean ?: false
            if (!success) {
                throw PaymentException("Tbank Init failed: ${result["Message"]}")
            }

            return PaymentInitResponse(
                orderId = order.id,
                paymentUrl = result["PaymentURL"] as? String ?: "",
                paymentId = result["PaymentId"]?.toString() ?: "",
            )
        }
    }

    override fun processWebhook(payload: Map<String, Any>): WebhookResult {
        if (!verifyWebhookSignature(payload)) {
            throw ForbiddenException("Invalid Tbank webhook signature")
        }
        val paymentId = payload["PaymentId"]?.toString()
            ?: throw PaymentException("Missing PaymentId in webhook payload")
        val success = payload["Status"]?.toString() == "CONFIRMED"
        return WebhookResult(paymentId = paymentId, success = success)
    }

    /**
     * Verifies a Tbank webhook signature.
     * Algorithm: take all fields except Token and Receipt, add SecretKey,
     * sort by key, concatenate values, SHA-256.
     */
    private fun verifyWebhookSignature(payload: Map<String, Any>): Boolean {
        val secretKey = appProperties.tbank.secretKey
        if (secretKey.isBlank()) return true // dev mode — skip verification

        val receivedToken = payload["Token"]?.toString() ?: return false

        val params = payload
            .filterKeys { it != "Token" && it != "Receipt" }
            .toMutableMap<String, Any>()
        params["Password"] = secretKey

        val concat = params.entries
            .sortedBy { it.key }
            .joinToString("") { it.value.toString() }

        val expectedToken = MessageDigest.getInstance("SHA-256")
            .digest(concat.toByteArray())
            .joinToString("") { "%02x".format(it) }

        // Constant-time comparison to prevent timing oracle attacks
        return MessageDigest.isEqual(expectedToken.toByteArray(Charsets.UTF_8), receivedToken.toByteArray(Charsets.UTF_8))
    }

    /** SHA-256 token as per Tbank docs: sort params by key, concat values, hash */
    private fun buildToken(terminalKey: String, amount: Long, orderId: String): String {
        val secretKey = appProperties.tbank.secretKey
        val params = mapOf(
            "TerminalKey" to terminalKey,
            "Amount" to amount.toString(),
            "OrderId" to orderId,
            "Description" to "Заказ #$orderId",
            "Password" to secretKey,
        )
        val concat = params.entries.sortedBy { it.key }.joinToString("") { it.value }
        return MessageDigest.getInstance("SHA-256")
            .digest(concat.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
