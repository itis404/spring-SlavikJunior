package com.coffeeshop.service

import com.coffeeshop.contracts.TokenPair

interface AuthService {
    fun firebaseVerify(idToken: String): TokenPair
    fun firebaseRegister(idToken: String, name: String): TokenPair
    fun refresh(refreshToken: String): TokenPair
    /** Deletes expired and revoked refresh tokens. Returns the count of deleted tokens. */
    fun cleanExpiredTokens(): Long
}
