package com.yu.paidleave.dto

import com.yu.paidleave.entity.LeaveRequest

fun LeaveRequest.toResponseDto(): LeaveRequestResponseDto {
    return LeaveRequestResponseDto(
        id = this.id,
        username = this.user.username,
        name = this.user.name,
        startDate = this.startDate,
        endDate = this.endDate,
        createdDate = this.createdDate,
        updatedDate = this.updatedDate,
        reason = this.reason,
        status = this.status,
        comment = this.comment,
        days = this.days
    )
}
