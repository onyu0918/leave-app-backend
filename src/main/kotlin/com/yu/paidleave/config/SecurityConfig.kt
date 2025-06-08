package com.yu.paidleave.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import com.yu.paidleave.security.JwtAuthenticationFilter
import org.springframework.security.config.annotation.web.invoke
import org.springframework.http.HttpMethod
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
////    @Bean
////    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
//
//    @Bean
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http {
//            cors { }
//            csrf { disable() }
//            authorizeHttpRequests {
//                authorize(HttpMethod.OPTIONS, "/**", permitAll)
//                authorize("/", permitAll)
//                authorize("/index.html", permitAll)
//                authorize("/main.dart.js", permitAll)
//                authorize("/flutter_bootstrap.js", permitAll)
//                authorize("/favicon.ico", permitAll)
//                authorize("/favicon.png", permitAll)
//
//                authorize("/assets/**", permitAll)
//                authorize("/canvaskit/**", permitAll)
//                authorize("/splash/**", permitAll)
//                authorize("/icons/**", permitAll)
//
//                authorize("/api/auth/login", permitAll)
//                authorize("/api/users/add", permitAll)
//                authorize("/api/leave/**", authenticated)
//
//                authorize(anyRequest, authenticated)
//            }
//            sessionManagement {
//                sessionCreationPolicy = SessionCreationPolicy.STATELESS
//            }
//            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
//        }
//
//        return http.build()
//    }
//
//}


import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {

//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val configuration = CorsConfiguration().apply {
//            allowedOrigins = listOf("*")
//            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
//            allowedHeaders = listOf("*")
//            allowCredentials = true
//            maxAge = 3600
//        }
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }  // cors 활성화
            csrf { disable() }
            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize("/", permitAll)
                authorize("/index.html", permitAll)
                authorize("/main.dart.js", permitAll)
                authorize("/flutter_bootstrap.js", permitAll)
                authorize("/favicon.ico", permitAll)
                authorize("/favicon.png", permitAll)

                authorize("/assets/**", permitAll)
                authorize("/canvaskit/**", permitAll)
                authorize("/splash/**", permitAll)
                authorize("/icons/**", permitAll)

                authorize("/api/auth/login", permitAll)
                authorize("/api/users/add", permitAll)
                authorize("/api/leave/**", authenticated)

                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }

        return http.build()
    }

}
