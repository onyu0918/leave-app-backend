package com.yu.paidleave.controller

import com.yu.paidleave.dto.LeaveRequestDto
import com.yu.paidleave.dto.LeaveStatusUpdateRequestDto
import com.yu.paidleave.dto.RejectReasonDto
import com.yu.paidleave.dto.LeaveRequestResponseDto
import com.yu.paidleave.entity.LeaveRequest
import com.yu.paidleave.dto.toResponseDto
import com.yu.paidleave.entity.LeaveStatus
import com.yu.paidleave.security.JwtUtil
import com.yu.paidleave.service.LeaveRequestService
import com.yu.paidleave.service.UserService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.AccessDeniedException
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/leave")
class LeaveRequestController(
    private val leaveRequestService: LeaveRequestService,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/apply")
    fun applyLeave(@Valid @RequestBody request: LeaveRequestDto, @RequestHeader("Authorization") authHeader: String): LeaveRequest {
        val username = extractUsernameFromAuthHeader(authHeader)
        val user = userService.findUserByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        return leaveRequestService.applyLeave(
            user,
            request.startDate,
            request.endDate,
            request.reason
        )
    }

    @DeleteMapping("/delete/{id}")
    fun deletePendingLeave(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String) {
        val username = extractUsernameFromAuthHeader(authHeader)
        leaveRequestService.deletePendingLeave(id, username)
    }

    @GetMapping("/list", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserLeaves(@RequestHeader("Authorization") authHeader: String): ResponseEntity<List<LeaveRequest>> {
        val username = extractUsernameFromAuthHeader(authHeader)
        val user = userService.findUserByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        val leaves = leaveRequestService.getUserLeaves(user.id)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(leaves)
    }

    private fun extractUsernameFromAuthHeader(authHeader: String): String {
        if (!authHeader.startsWith("Bearer ")) {
            throw RuntimeException("Authorization 헤더 형식이 잘못되었습니다.")
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        return jwtUtil.extractUsername(token)
    }

    @PostMapping("/{id}/approve")
    fun approve(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String
    ): LeaveRequest {
        checkAdmin(jwtUtil, authHeader)
        return leaveRequestService.approveLeave(id)
    }

    @PostMapping("/{id}/reject")
    fun reject(
        @PathVariable id: Long,
        @RequestBody rejectDto: RejectReasonDto,
        @RequestHeader("Authorization") authHeader: String
    ): LeaveRequest {
        checkAdmin(jwtUtil, authHeader)
        return leaveRequestService.rejectLeave(id, rejectDto.reason)
    }

    private fun checkAdmin(jwtUtil: JwtUtil, authHeader: String) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val username = jwtUtil.extractUsername(token)
        val user = userService.findUserByUsername(username)
            ?: throw IllegalArgumentException("User not found")
        if (!user.isAdmin) {
            throw AccessDeniedException("관리자 권한이 필요합니다.")
        }
    }

//    @GetMapping("/admin/leave-requests")
//    fun getAllLeaveRequests(): List<LeaveRequestResponseDto> {
//        return leaveRequestService.getAllLeaveRequests().map { it.toResponseDto() }
//    }
//    @GetMapping("/admin/leave-requests", produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun getAllLeaveRequests(@RequestHeader("Authorization") authHeader: String): ResponseEntity<List<LeaveRequestResponseDto>> {
//        val username = extractUsernameFromAuthHeader(authHeader)
//        val user = userService.findUserByUsername(username)
//            ?: throw IllegalArgumentException("User not found")
//
//        if (!user.isAdmin) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
//        }
//
//        val leaveRequests = leaveRequestService.getAllLeaveRequests()
//            .map { it.toResponseDto() }
//
//        return ResponseEntity.ok()
//            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
//            .body(leaveRequests)
//    }

//    @GetMapping("/admin/leave-requests")
//    fun getFilteredLeaveRequests(
//        @RequestParam(defaultValue = "0") page: Int,
//        @RequestParam(defaultValue = "10") size: Int,
//        @RequestParam(required = false) status: LeaveStatus?,
//        @RequestParam(required = false) username: String?,
//        @RequestParam(defaultValue = "6") months: Int,
//        @RequestHeader("Authorization") authHeader: String
//    ): ResponseEntity<Page<LeaveRequestResponseDto>> {
//
//        val admin = extractUsernameFromAuthHeader(authHeader)
//        val user = userService.findUserByUsername(admin)
//            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
//
//        if (!user.isAdmin) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
//
//        val results = leaveRequestService.getFilteredLeaveRequests(page, size, status, username, months)
//            .map { it.toResponseDto() }
//
//        return ResponseEntity.ok(results)
//    }
    @GetMapping("/admin/leave-requests", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFilteredLeaveRequests(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: LeaveStatus?,
        @RequestParam(required = false) username: String?,
        @RequestParam(defaultValue = "6") months: Int,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Any> {
        val admin = extractUsernameFromAuthHeader(authHeader)
        val user = userService.findUserByUsername(admin)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        if (!user.isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val results = leaveRequestService.getFilteredLeaveRequests(page, size, status, username, months)
            .map { it.toResponseDto() }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(results)
    }

    @PatchMapping("/admin/{id}/status")
    fun updateLeaveStatus(
        @PathVariable id: Long,
        @RequestBody request: LeaveStatusUpdateRequestDto
    ): ResponseEntity<String> {
        leaveRequestService.updateLeaveStatus(id, request.status, request.rejectReason)
        return ResponseEntity.ok("Leave status updated")
    }
}