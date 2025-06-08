package com.yu.paidleave.service

import com.yu.paidleave.entity.User
import com.yu.paidleave.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDate

@Service
class UserService(private val userRepository: UserRepository) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun addUser(username: String, password: String, name: String, isAdmin: Boolean, joinDate: LocalDate): User {
        val encodedPassword = passwordEncoder.encode(password)
        val newUser = User(
            username = username,
            name = name,
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


    fun changePassword(username: String, newPassword: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
//        if (!passwordEncoder.matches(oldPassword, user.password)) return false

        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        return true
    }

    fun resetPasswordToDefault(username: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        user.password = passwordEncoder.encode("1234")
        userRepository.save(user)
        return true
    }

    fun deleteUserByUsername(username: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        userRepository.delete(user)
        return true
    }
}