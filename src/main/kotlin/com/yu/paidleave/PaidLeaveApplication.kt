package com.yu.paidleave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@SpringBootApplication
@EnableJpaAuditing
class PaidLeaveApplication

fun main(args: Array<String>) {
	runApplication<PaidLeaveApplication>(*args)
}
