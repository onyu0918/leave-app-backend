package com.yu.paidleave.dto

import com.yu.paidleave.entity.LeaveRequest
import com.yu.paidleave.dto.LeaveRequestResponseDto

fun LeaveRequest.toResponseDto(): LeaveRequestResponseDto {
    return LeaveRequestResponseDto(
        id = this.id,
        username = this.user.username,
        startDate = this.startDate,
        endDate = this.endDate,
        reason = this.reason,
        status = this.status,
        rejectReason = this.rejectReason
    )
}
