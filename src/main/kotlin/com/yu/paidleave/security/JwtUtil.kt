package com.yu.paidleave.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val expirationMs = 1000 * 60 * 60 * 10L // 10시간

    private val SECRET = "this-is-a-very-very-secret-and-long-key-1234567890"
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(SECRET.toByteArray())

    fun generateToken(username: String, isAdmin: Boolean): String {
        return Jwts.builder()
            .setSubject(username)
            .claim("isAdmin", isAdmin)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun validateToken(token: String, username: String): Boolean {
        return try {
            val extractedUsername = extractUsername(token)
            extractedUsername == username
        } catch (e: Exception) {
            println("JWT FILE: ${e.message}")
            false
        }
    }
}