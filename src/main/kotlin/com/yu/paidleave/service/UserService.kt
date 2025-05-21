package com.yu.paidleave.service

import com.yu.paidleave.entity.User
import com.yu.paidleave.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDate

@Service
class UserService(private val userRepository: UserRepository) {
    private val passwordEncoder = BCryptPasswordEncoder()
    fun addUser(username: String, password: String, isAdmin: Boolean, joinDate: LocalDate): User {
        val encodedPassword = passwordEncoder.encode(password)
        val newUser = User(
            username = username,
            password = encodedPassword,
            isAdmin = isAdmin,
            joinDate = joinDate
        )
        return userRepository.save(newUser)
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun findUserByUsername(username: String): User? =
        userRepository.findByUsername(username)
}