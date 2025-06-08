package com.yu.paidleave.controller

import com.yu.paidleave.entity.User
import com.yu.paidleave.repository.UserRepository
import com.yu.paidleave.security.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val isAdmin: Boolean)

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userRepository: UserRepository, private val jwtUtil: JwtUtil) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val user = userRepository.findByUsername(request.username)
            ?: return ResponseEntity.badRequest().body("ユーザーを見つけることができませんでした。")

        val passwordEncoder = BCryptPasswordEncoder()
        if (!passwordEncoder.matches(request.password, user.password)) {
            return ResponseEntity.badRequest().body("入力されたパスワードが正しくありません。")
        }

        val token = jwtUtil.generateToken(user.username, user.isAdmin)
        return ResponseEntity.ok(LoginResponse(token, user.isAdmin))
    }
}