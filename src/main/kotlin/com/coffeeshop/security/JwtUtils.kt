package com.coffeeshop.security

import com.coffeeshop.config.AppProperties
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtils(private val appProperties: AppProperties) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(appProperties.jwt.secret.toByteArray(Charsets.UTF_8))
    }

    @PostConstruct
    fun validateSecret() {
        val secret = appProperties.jwt.secret
        if (secret == "dev-secret-key-please-change-in-production-at-least-256-bits") {
            throw IllegalStateException(
                "JWT secret is still the default placeholder. Set the JWT_SECRET environment variable before starting the application."
            )
        }
        val byteLen = secret.toByteArray(Charsets.UTF_8).size
        if (byteLen < 32) {
            throw IllegalStateException(
                "JWT secret is too short ($byteLen bytes) — minimum 32 bytes (256 bits) required"
            )
        }
    }

    fun generateAccessToken(userId: Long): String =
        Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + appProperties.jwt.accessTokenExpiration))
            .signWith(key)
            .compact()

    fun validateAndExtractUserId(token: String): Long? =
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
                .toLong()
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
}
