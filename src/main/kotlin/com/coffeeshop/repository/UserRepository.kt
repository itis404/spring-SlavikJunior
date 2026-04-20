package com.coffeeshop.repository

import com.coffeeshop.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserRepository : JpaRepository<User, Long> {

    fun findByPhone(phone: String): User?

    fun existsByPhone(phone: String): Boolean

    // Custom JPQL — users registered after a given date (university: non-derived method)
    @Query("SELECT u FROM User u WHERE u.createdAt > :since AND u.deletedAt IS NULL")
    fun findUsersRegisteredAfter(@Param("since") since: LocalDateTime): List<User>
}
