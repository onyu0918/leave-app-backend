package com.yu.paidleave.service

import com.yu.paidleave.entity.LeaveRequest
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
    fun applyLeave(user: User, startDate: LocalDate, endDate: LocalDate, reason: String, days: Double): LeaveRequest {
        val leave = LeaveRequest(
            user = user,
            startDate = startDate,
            endDate = endDate,
            reason = reason,
            days = days,
            status = 0
        )
        return leaveRequestRepository.save(leave)
    }

    fun deletePendingLeave(id: Long, username: String) {
        val leave = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("該当の休暇申請を見つけることができませんでした。") }

        if (leave.user.username != username) {
            throw IllegalAccessException("ご自身の休暇のみ削除可能です。")
        }

        if (leave.status != 0) {
            throw IllegalStateException("申請中の休暇のみ削除可能です。")
        }

        leaveRequestRepository.delete(leave)
    }

    fun adminDeletePendingLeave(id: Long, username: String) {
        val leave = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("該当の休暇申請を見つけることができませんでした。") }

        leaveRequestRepository.delete(leave)
    }

    fun countPendingLeaves(): Int {
        return leaveRequestRepository.countByStatus(0)
    }


    fun getUserLeaves(userId: Long): List<LeaveRequest> =
        leaveRequestRepository.findByUserId(userId)


    fun approveLeave(id: Long): LeaveRequest {
        val lr = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("該当する休暇申請が見つかりませんでした。: $id") }
        lr.status = 1
        return leaveRequestRepository.save(lr)
    }

    fun rejectLeave(id: Long, comment: String): LeaveRequest {
        val lr = leaveRequestRepository.findById(id)
            .orElseThrow { IllegalArgumentException("該当する休暇申請が見つかりませんでした。: $id") }
        lr.status = 2
        lr.comment = comment
        return leaveRequestRepository.save(lr)
    }

    fun getAllLeaveRequests(): List<LeaveRequest> {
//        return leaveRequestRepository.findAll()
        return leaveRequestRepository.findAllByStatusNot(2)
    }

    fun updateLeaveStatus(id: Long, status: Int, comment: String?) {
        val leave = leaveRequestRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("該当する休暇申請が見つかりませんでした。")

        leave.status = status
        leave.comment = if (leave.status == 2) comment else null

        leaveRequestRepository.save(leave)
        leaveRequestRepository.save(leave)
    }

    fun updateUserLeaveStatus(id: Long, status: Int) {
        val leave = leaveRequestRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("該当する休暇申請が見つかりませんでした。")

        leave.status = status

        leaveRequestRepository.save(leave)
        leaveRequestRepository.save(leave)
    }

    fun getRecentLeaveRequests(): List<LeaveRequest> {
        val sixMonthsAgo = LocalDate.now().minusMonths(6)
        return leaveRequestRepository.findByStartDateAfter(sixMonthsAgo)
    }

    fun getFilteredLeaveRequests(page: Int, size: Int, status: Int?, name: String?, months: Int): Page<LeaveRequest> {
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