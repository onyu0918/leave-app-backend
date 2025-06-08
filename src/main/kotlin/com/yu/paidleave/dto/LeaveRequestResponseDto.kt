package com.yu.paidleave.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class LeaveRequestResponseDto(
    val id: Long,
    val username: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdDate: LocalDateTime? = null,
    val updatedDate: LocalDateTime? = null,
    val reason: String,
    val status: Int,
    val comment: String? = null,
    val days: Double
)
