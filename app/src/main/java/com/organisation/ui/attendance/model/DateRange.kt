package com.organisation.ui.attendance.model

import java.time.LocalDate

data class DateRange(
    val start: LocalDate,
    val end: LocalDate
) {
    fun dayCount(): Int = (end.toEpochDay() - start.toEpochDay()).toInt() + 1
    operator fun contains(date: LocalDate): Boolean = date in start..end
}
