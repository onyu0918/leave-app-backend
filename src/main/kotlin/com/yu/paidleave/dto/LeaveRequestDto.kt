package com.yu.paidleave.dto

import java.time.LocalDate
import jakarta.validation.constraints.*

data class LeaveRequestDto(
//    @field:NotBlank(message = "이름은 필수입니다.")
//    val username: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    @field:Size(min = 10, message = "사유는 최소 10자 이상이어야 합니다.")
    val reason: String
)