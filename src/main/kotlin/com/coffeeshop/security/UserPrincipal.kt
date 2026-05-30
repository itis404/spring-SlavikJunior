package com.coffeeshop.security

import com.coffeeshop.entity.Role
import com.coffeeshop.entity.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable

/**
 * Serializable principal stored in Spring Session (Redis).
 * Holds only primitive fields — no JPA entities or Hibernate proxies.
 */
data class UserPrincipal(
    val userId: Long,
    private val phone: String,
    private val passwordHash: String?,
    private val role: Role,
    private val active: Boolean,
) : UserDetails, Serializable {

    companion object {
        fun from(user: User) = UserPrincipal(
            userId = user.id,
            phone = user.phone,
            passwordHash = user.passwordHash,
            role = user.role,
            active = user.deletedAt == null,
        )
    }

    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    override fun getPassword(): String? = passwordHash
    override fun getUsername(): String = phone
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = active
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = active
}
