package com.yu.paidleave.security

import com.yu.paidleave.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader = request.getHeader("Authorization")

        if (request.servletPath == "/api/auth/login") {
            filterChain.doFilter(request, response)
            return
        }

        println("SecurityContext 인증 상태: ${SecurityContextHolder.getContext().authentication}")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            try {
                val username = jwtUtil.extractUsername(token)

                if (SecurityContextHolder.getContext().authentication == null) {
                    val user = userRepository.findByUsername(username)
                        ?: run {
                            response.status = HttpServletResponse.SC_UNAUTHORIZED
                            response.writer.write("사용자를 찾을 수 없습니다.")
                            return
                        }

                    if (jwtUtil.validateToken(token, user.username)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            user, null, emptyList()
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            } catch (e: ExpiredJwtException) {
                println("JWT expired: ${e.message}")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("JWT 토큰이 만료되었습니다.")
                return
            } catch (e: Exception) {
                println("JWT error: ${e.message}")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("JWT 처리 오류: ${e.message}")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
