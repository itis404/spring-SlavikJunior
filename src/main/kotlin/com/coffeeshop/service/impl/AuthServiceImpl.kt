package com.coffeeshop.service.impl

import com.coffeeshop.config.AppProperties
import com.coffeeshop.contracts.TokenPair
import com.coffeeshop.entity.RefreshToken
import com.coffeeshop.entity.Role
import com.coffeeshop.entity.User
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.exception.PhoneAlreadyRegisteredException
import com.coffeeshop.exception.UnauthorizedException
import com.coffeeshop.repository.RefreshTokenRepository
import com.coffeeshop.repository.UserRepository
import com.coffeeshop.security.FirebaseTokenVerifier
import com.coffeeshop.security.JwtUtils
import com.coffeeshop.service.AuthService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val firebaseTokenVerifier: FirebaseTokenVerifier,
    private val jwtUtils: JwtUtils,
    private val appProperties: AppProperties,
) : AuthService {

    override fun firebaseVerify(idToken: String): TokenPair {
        val phone = firebaseTokenVerifier.verifyAndGetPhone(idToken)
        val user = userRepository.findByPhone(phone)
            ?: throw EntityNotFoundException("User", phone)
        return issueTokenPair(user)
    }

    override fun firebaseRegister(idToken: String, name: String): TokenPair {
        val phone = firebaseTokenVerifier.verifyAndGetPhone(idToken)
        if (userRepository.existsByPhone(phone)) throw PhoneAlreadyRegisteredException()
        val user = userRepository.save(User(name = name, phone = phone, role = Role.CLIENT))
        return issueTokenPair(user)
    }

    override fun refresh(refreshToken: String): TokenPair {
        val token = refreshTokenRepository.findByToken(refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")
        if (!token.isValid()) throw UnauthorizedException("Refresh token expired or revoked")
        token.revoked = true
        return issueTokenPair(token.user)
    }

    private fun issueTokenPair(user: User): TokenPair {
        val accessToken = jwtUtils.generateAccessToken(user.id)
        val rawRefresh = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusSeconds(appProperties.jwt.refreshTokenExpiration / 1000)
        refreshTokenRepository.save(RefreshToken(user = user, token = rawRefresh, expiresAt = expiresAt))
        return TokenPair(accessToken = accessToken, refreshToken = rawRefresh, userId = user.id)
    }

    @Transactional
    override fun cleanExpiredTokens(): Long =
        refreshTokenRepository.deleteExpiredAndRevoked(LocalDateTime.now()).toLong()
}
