package com.yu.paidleave.dto

data class LeaveStatusUpdateRequestDto(
    val status: Int,
    val comment: String? = null
)