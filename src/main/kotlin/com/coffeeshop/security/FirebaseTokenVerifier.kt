package com.coffeeshop.security

import com.coffeeshop.exception.UnauthorizedException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import org.springframework.stereotype.Component

@Component
class FirebaseTokenVerifier(private val firebaseApp: FirebaseApp?) {

    fun verifyAndGetPhone(idToken: String): String {
        val app = firebaseApp
            ?: throw UnauthorizedException("Firebase не инициализирован — задайте FIREBASE_SERVICE_ACCOUNT_BASE64")
        return try {
            val decoded = FirebaseAuth.getInstance(app).verifyIdToken(idToken)
            decoded.claims["phone_number"] as? String
                ?: throw UnauthorizedException("Firebase ID Token не содержит номера телефона")
        } catch (e: FirebaseAuthException) {
            throw UnauthorizedException("Недействительный Firebase ID Token: ${e.authErrorCode?.name}")
        }
    }
}
