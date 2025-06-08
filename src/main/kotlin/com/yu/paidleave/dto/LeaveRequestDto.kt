package com.yu.paidleave.dto

import java.time.LocalDate
import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class LeaveRequestDto(

    val startDate: LocalDate,

    val endDate: LocalDate,

    val createdDate: LocalDateTime? = null,

    val updatedDate: LocalDateTime? = null,

    @field:Size(min = 5, message = "理由は最低でも5文字以上入力する必要があります。")
    val reason: String,

    val days: Double
)