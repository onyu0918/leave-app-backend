package com.yu.paidleave.service

import com.yu.paidleave.entity.LeaveRequest
import com.yu.paidleave.entity.LeaveStatus
import com.yu.paidleave.entity.User
import com.yu.paidleave.repository.LeaveRequestRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.LocalDate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@Service
class LeaveRequestService(private val leaveRequestRepository: LeaveRequestRepository) {
    fun applyLeave(user: User, startDate: LocalDate, endDate: LocalDate, reason: String): LeaveRequest {
        val leave = LeaveRequest(
            user = user,
            startDate = startDate,
            endDate = endDate,
            reason = reason,
            status = LeaveStatus.PENDING
        )
        return leaveRequestRepository.save(leave)
    }

    fun deletePendingLeave(id: Long, username: String) {
        val leave = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 연차 신청을 찾을 수 없습니다.") }

        if (leave.user.username != username) {
            throw IllegalAccessException("본인의 연차만 삭제할 수 있습니다.")
        }

        if (leave.status != LeaveStatus.PENDING) {
            throw IllegalStateException("대기 중(PENDING) 상태의 연차만 삭제할 수 있습니다.")
        }

        leaveRequestRepository.delete(leave)
    }


    fun getUserLeaves(userId: Long): List<LeaveRequest> =
        leaveRequestRepository.findByUserId(userId)


    fun approveLeave(id: Long): LeaveRequest {
        val lr = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("LeaveRequest not found: $id") }
        lr.status = LeaveStatus.APPROVED
        return leaveRequestRepository.save(lr)
    }

    fun rejectLeave(id: Long, rejectReason: String): LeaveRequest {
        val lr = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("LeaveRequest not found: $id") }
        lr.status = LeaveStatus.REJECTED
        lr.rejectReason = rejectReason
        return leaveRequestRepository.save(lr)
    }

    fun getAllLeaveRequests(): List<LeaveRequest> {
        return leaveRequestRepository.findAll()
    }

    fun updateLeaveStatus(id: Long, status: String, rejectReason: String?) {
        val leave = leaveRequestRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Leave request not found")

        leave.status = LeaveStatus.valueOf(status.uppercase())
        leave.rejectReason = if (leave.status == LeaveStatus.REJECTED) rejectReason else null

        leaveRequestRepository.save(leave)
    }

    fun getRecentLeaveRequests(): List<LeaveRequest> {
        val sixMonthsAgo = LocalDate.now().minusMonths(6)
        return leaveRequestRepository.findByStartDateAfter(sixMonthsAgo)
    }

    fun getFilteredLeaveRequests(page: Int, size: Int, status: LeaveStatus?, name: String?, months: Int): Page<LeaveRequest> {

        val pageable: Pageable = PageRequest.of(page, size)
        val name = name ?: ""
        if (months == 0) {
            return leaveRequestRepository.findAllByFilters(status, name, pageable)
        } else {
            val now = LocalDate.now()
            val fromDate = now.minusMonths(months.toLong())

            return leaveRequestRepository.findAllByFilters(status, name, fromDate, pageable)
        }
    }
}