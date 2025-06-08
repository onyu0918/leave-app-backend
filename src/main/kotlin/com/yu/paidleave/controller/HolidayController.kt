package com.yu.paidleave.controller

import com.yu.paidleave.entity.Holidays
import com.yu.paidleave.service.HolidayService
import com.yu.paidleave.service.UserService
import com.yu.paidleave.security.JwtUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/holidays")
class HolidayController(
    private val holidayService: HolidayService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    fun getAllHolidays(): ResponseEntity<List<Holidays>> {
        val response = holidayService.getAllHolidays()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(response)
    }

    private fun extractUsernameFromAuthHeader(authHeader: String): String {
        val token = authHeader.removePrefix("Bearer ").trim()
        return jwtUtil.extractUsername(token)
    }
}
