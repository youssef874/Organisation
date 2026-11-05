package com.organisation.domaine.attendance

import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRepository {

    fun observeMonthlyAttendanceReport(month: Int, year: Int): Flow<List<AttendanceRecord>>

    fun observeUserAttendanceReportViaRanger(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<AttendanceRecord>>

    fun observeUserAttendanceStatusReport(
        year: Int,
        attendanceStatus: AttendanceStatus
    ): Flow<Int>

    suspend fun getAttendanceRapport(
        day: LocalDate
    ): AttendanceRecord?

    suspend fun cancelDayOffRequest(
        day: LocalDate
    )
}
