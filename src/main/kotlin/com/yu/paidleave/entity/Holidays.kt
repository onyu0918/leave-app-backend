package com.yu.paidleave.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "holidays")
data class Holidays (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "holiday_date", nullable = false)
    val holidayDate: LocalDate,

    @Column(name = "holiday_name", nullable = false, length = 100)
    val holidayName: String
)