package com.organisation.domaine.attendance.model

import java.time.LocalDate

data class AttendanceRecord(
    val id: String,
    val dayOfMonth: Int,
    val dayOfWeek: String,
    val checkIn: String? = null,
    val checkOut: String? = null,
    val status: AttendanceStatus,
    val requestReason: String? = null,
    val monthOfYear: Int = LocalDate.now().monthValue,
    val year: Int = LocalDate.now().year
) {
    val date: LocalDate
        get()  {
            return LocalDate.of(year, monthOfYear, dayOfMonth)
        }
}
