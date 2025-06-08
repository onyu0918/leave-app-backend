package com.yu.paidleave.service

import com.yu.paidleave.entity.Holidays
import com.yu.paidleave.repository.HolidayRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class HolidayService(
    private val holidayRepository: HolidayRepository
) {
    fun getAllHolidays(): List<Holidays> = holidayRepository.findAll()
}
