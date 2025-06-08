package com.yu.paidleave.controller

import com.fasterxml.jackson.databind.type.LogicalType
import com.yu.paidleave.dto.LeaveRequestDto
import com.yu.paidleave.dto.LeaveStatusUpdateRequestDto
import com.yu.paidleave.dto.CommentDto
import com.yu.paidleave.entity.LeaveRequest
import com.yu.paidleave.dto.toResponseDto
import com.yu.paidleave.security.JwtUtil
import com.yu.paidleave.service.LeaveRequestService
import com.yu.paidleave.service.UserService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.AccessDeniedException
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import java.util.Date

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
            ?: throw IllegalArgumentException("該当するユーザーが見つかりませんでした。")

        return leaveRequestService.applyLeave(
            user,
            request.startDate,
            request.endDate,
            request.reason,
            request.days
        )
    }

    @DeleteMapping("/delete/{id}")
    fun deletePendingLeave(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String) {
        val username = extractUsernameFromAuthHeader(authHeader)
        leaveRequestService.deletePendingLeave(id, username)
    }

    @DeleteMapping("/admin/delete/{id}")
    fun adminDeletePendingLeave(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String) {
        val username = extractUsernameFromAuthHeader(authHeader)
        leaveRequestService.adminDeletePendingLeave(id, username)
    }

    @GetMapping("/count/pending")
    fun getPendingLeaveCount(): ResponseEntity<Map<String, Int>> {
        val count = leaveRequestService.countPendingLeaves()
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @GetMapping("/list", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getUserLeaves(@RequestHeader("Authorization") authHeader: String): ResponseEntity<List<LeaveRequest>> {
        val username = extractUsernameFromAuthHeader(authHeader)
        val user = userService.findUserByUsername(username)
            ?: throw IllegalArgumentException("該当するユーザーが見つかりませんでした。")

        val leaves = leaveRequestService.getUserLeaves(user.id)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(leaves)
    }

    @GetMapping("/lists/{username}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getLeaves(@PathVariable username: String): ResponseEntity<List<LeaveRequest>> {
        val user = userService.findUserByUsername(username)
            ?: throw IllegalArgumentException("該当するユーザーが見つかりませんでした。")

        val leaves = leaveRequestService.getUserLeaves(user.id)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(leaves)
    }

    @GetMapping("/get-sysdate", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getSysdate(): ResponseEntity<LocalDate> {

        val sysDate = LocalDate.now()

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8")
            .body(sysDate)
    }

    private fun extractUsernameFromAuthHeader(authHeader: String): String {
        if (!authHeader.startsWith("Bearer ")) {
            throw RuntimeException("Authorizationヘッダーの形式に誤りがあります。")
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
        @RequestBody rejectDto: CommentDto,
        @RequestHeader("Authorization") authHeader: String
    ): LeaveRequest {
        checkAdmin(jwtUtil, authHeader)
        return leaveRequestService.rejectLeave(id, rejectDto.comment)
    }

    private fun checkAdmin(jwtUtil: JwtUtil, authHeader: String) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val username = jwtUtil.extractUsername(token)
        val user = userService.findUserByUsername(username)
            ?: throw IllegalArgumentException("該当するユーザーが見つかりませんでした。")
        if (!user.isAdmin) {
            throw AccessDeniedException("この操作を行うには管理者権限が必要です。")
        }
    }

    @GetMapping("/admin/leave-requests", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getFilteredLeaveRequests(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: Int?,
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
        leaveRequestService.updateLeaveStatus(id, request.status, request.comment)
        return ResponseEntity.ok("休暇のステータスが正常に更新されました。")
    }

    @PatchMapping("/{id}/status")
    fun updateUserLeaveStatus(
        @PathVariable id: Long,
        @RequestBody request: LeaveStatusUpdateRequestDto
    ): ResponseEntity<String> {
        leaveRequestService.updateUserLeaveStatus(id, request.status)
        return ResponseEntity.ok("休暇のステータスが正常に更新されました。")
    }


    @GetMapping("/admin/allleave-requests", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllLeaveEvents(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Map<String, List<String>>> {
        val username = extractUsernameFromAuthHeader(authHeader)
        val user = userService.findUserByUsername(username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        if (!user.isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val leaveRequests = leaveRequestService.getAllLeaveRequests()
        val eventMap = mutableMapOf<String, MutableList<String>>()

        for (leave in leaveRequests) {
            val start = leave.startDate
            val end = leave.endDate
            val name = leave.user.name

            var date = start
            while (!date.isAfter(end)) {
                val key = date.toString()
                eventMap.computeIfAbsent(key) { mutableListOf() }.add(name)
                date = date.plusDays(1)
            }
        }

        return ResponseEntity.ok(eventMap)
    }
}