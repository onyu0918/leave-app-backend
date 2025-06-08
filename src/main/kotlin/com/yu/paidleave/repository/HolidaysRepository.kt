package com.yu.paidleave.repository

import com.yu.paidleave.entity.Holidays
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface HolidayRepository : JpaRepository<Holidays, Long> {
    fun findByHolidayDate(holidayDate: LocalDate): Holidays?
}