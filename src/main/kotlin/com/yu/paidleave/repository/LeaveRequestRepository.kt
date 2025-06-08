package com.yu.paidleave.repository

import com.yu.paidleave.entity.LeaveRequest
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface LeaveRequestRepository : JpaRepository<LeaveRequest, Long> {
    fun findByUserId(userId: Long): List<LeaveRequest>

    fun countByStatus(status: Int): Int

    fun findAllByStatusNot(status: Int): List<LeaveRequest>

    @Query("""
        SELECT l FROM LeaveRequest l 
        WHERE l.startDate >= :startDate 
        ORDER BY l.createdDate DESC
    """)
    fun findByStartDateAfter(@Param("startDate") startDate: LocalDate): List<LeaveRequest>

    @Query("""
        SELECT lr FROM LeaveRequest lr
        JOIN lr.user u
        WHERE (:status IS NULL OR lr.status = :status)
        AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))
        AND lr.startDate >= :startDate
        ORDER BY lr.createdDate DESC
    """)
    fun findAllByFilters(
        @Param("status") status: Int?,
        @Param("username") username: String?,
        @Param("startDate") startDate: LocalDate,
        pageable: Pageable
    ): Page<LeaveRequest>

    @Query("""
        SELECT lr FROM LeaveRequest lr
        JOIN lr.user u
        WHERE (:status IS NULL OR lr.status = :status)
        AND (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))
        ORDER BY lr.createdDate DESC
    """)
    fun findAllByFilters(
        @Param("status") status: Int?,
        @Param("username") username: String?,
        pageable: Pageable
    ): Page<LeaveRequest>
}
