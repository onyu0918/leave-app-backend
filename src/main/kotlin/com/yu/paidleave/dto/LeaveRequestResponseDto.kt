package com.yu.paidleave.dto

import com.yu.paidleave.entity.LeaveStatus
import java.time.LocalDate

data class LeaveRequestResponseDto(
    val id: Long,
    val username: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: String,
    val status: LeaveStatus,
    val rejectReason: String? = null
)
