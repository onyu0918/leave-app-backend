package com.yu.paidleave.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "leave_requests")
@EntityListeners(AuditingEntityListener::class)
open class LeaveRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    open val user: User,

    @Column(nullable = false)
    open val startDate: LocalDate,

    @Column(nullable = false)
    open val endDate: LocalDate,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    open var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = false)
    open var updatedDate: LocalDateTime? = null,

    @Column(nullable = false)
    open val reason: String,

    @Column(nullable = false)
    open var status: Int = 0,

    @Column(nullable = false)
    open var days: Double = 0.0,

    open var comment: String? = null
)