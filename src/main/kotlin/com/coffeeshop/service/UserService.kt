package com.coffeeshop.service

import com.coffeeshop.contracts.ProfileDto
import com.coffeeshop.dto.UpdateProfileRequest
import com.coffeeshop.dto.UserAdminDto

interface UserService {
    fun getProfile(userId: Long): ProfileDto
    fun updateProfile(userId: Long, request: UpdateProfileRequest): ProfileDto
    fun listAll(): List<UserAdminDto>
    fun adminCreateUser(name: String, phone: String, email: String?, bonusPoints: Int): UserAdminDto
}
