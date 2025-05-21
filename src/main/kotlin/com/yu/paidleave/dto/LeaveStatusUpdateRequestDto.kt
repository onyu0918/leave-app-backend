package com.yu.paidleave.dto

data class LeaveStatusUpdateRequestDto(
    val status: String,
    val rejectReason: String? = null
)