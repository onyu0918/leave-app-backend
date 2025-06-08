package com.yu.paidleave.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    val isAdmin: Boolean = false,

    val joinDate: LocalDate
)