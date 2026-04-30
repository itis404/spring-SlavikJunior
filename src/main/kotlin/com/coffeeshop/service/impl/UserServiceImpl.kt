package com.coffeeshop.service.impl

import com.coffeeshop.contracts.ProfileDto
import com.coffeeshop.dto.UpdateProfileRequest
import com.coffeeshop.dto.UserAdminDto
import com.coffeeshop.dto.toProfileDto
import com.coffeeshop.dto.toUserAdminDto
import com.coffeeshop.entity.Role
import com.coffeeshop.entity.User
import com.coffeeshop.exception.EntityNotFoundException
import com.coffeeshop.exception.PhoneAlreadyRegisteredException
import com.coffeeshop.repository.UserRepository
import com.coffeeshop.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    @Transactional(readOnly = true)
    override fun getProfile(userId: Long): ProfileDto {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User", userId) }
        return user.toProfileDto()
    }

    override fun updateProfile(userId: Long, request: UpdateProfileRequest): ProfileDto {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User", userId) }
        request.name?.let { user.name = it }
        request.email?.let { user.email = it }
        return user.toProfileDto()
    }

    @Transactional(readOnly = true)
    override fun listAll(): List<UserAdminDto> =
        userRepository.findAll().map { it.toUserAdminDto() }

    override fun adminCreateUser(name: String, phone: String, email: String?, bonusPoints: Int): UserAdminDto {
        if (userRepository.existsByPhone(phone)) throw PhoneAlreadyRegisteredException()
        val user = userRepository.save(
            User(name = name, phone = phone, email = email, bonusPoints = bonusPoints, role = Role.CLIENT)
        )
        return user.toUserAdminDto()
    }
}
