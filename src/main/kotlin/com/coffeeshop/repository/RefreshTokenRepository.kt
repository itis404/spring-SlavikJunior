package com.coffeeshop.repository

import com.coffeeshop.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByToken(token: String): RefreshToken?

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId")
    fun revokeAllByUserId(@Param("userId") userId: Long)

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now OR rt.revoked = true")
    fun deleteExpiredAndRevoked(@Param("now") now: LocalDateTime): Int
}
