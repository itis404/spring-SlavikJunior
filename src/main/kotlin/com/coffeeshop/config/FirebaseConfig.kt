package com.coffeeshop.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Base64

@Configuration
class FirebaseConfig(private val firebaseProperties: FirebaseProperties) {

    private val log = LoggerFactory.getLogger(FirebaseConfig::class.java)

    @Bean
    fun firebaseApp(): FirebaseApp? {
        val encoded = firebaseProperties.serviceAccountBase64
        if (encoded.isBlank()) {
            log.warn("Firebase: FIREBASE_SERVICE_ACCOUNT_BASE64 not set — FCM push disabled")
            return null
        }

        if (FirebaseApp.getApps().isNotEmpty()) {
            return FirebaseApp.getInstance()
        }

        return try {
            val json = Base64.getDecoder().decode(encoded).inputStream()
            val credentials = GoogleCredentials.fromStream(json)
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
            FirebaseApp.initializeApp(options).also {
                log.info("Firebase: initialized app '{}'", it.name)
            }
        } catch (e: Exception) {
            log.error("Firebase: failed to initialize — FCM push disabled", e)
            null
        }
    }
}
