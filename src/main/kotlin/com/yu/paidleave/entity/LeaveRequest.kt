package com.yu.paidleave.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "leave_requests")
data class LeaveRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Column(nullable = false)
    val reason: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: LeaveStatus = LeaveStatus.PENDING,

    var rejectReason: String? = null
)