package com.yu.paidleave.util

import com.yu.paidleave.security.JwtUtil
import com.yu.paidleave.service.UserService
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component

@Component
class AuthHelper(
    private val jwtUtil: JwtUtil,
    private val userService: UserService
) {
    fun isAdmin(authHeader: String): Boolean {
        val token = authHeader.removePrefix("Bearer ").trim()
        val username = jwtUtil.extractUsername(token)
        val user = userService.getUserByUsername(username)
            ?: throw AccessDeniedException("ユーザーが見つかりません。")

        return user.isAdmin
    }

    fun extractUsername(authHeader: String): String {
        val token = authHeader.removePrefix("Bearer ").trim()
        return jwtUtil.extractUsername(token)
    }
}