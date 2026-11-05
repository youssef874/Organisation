package com.organisation.domaine.attendance

import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.DayOfWeek
import java.time.LocalDate

class FakeAttendanceRepository : AttendanceRepository {
    override fun observeMonthlyAttendanceReport(
        month: Int,
        year: Int
    ): Flow<List<AttendanceRecord>> {
        return flowOf(
            if (month == 9 && year == 2026) dummyRecords.values.toList()
            else emptyList()
        )
    }

    override fun observeUserAttendanceReportViaRanger(
        start: LocalDate,
        end: LocalDate
    ): Flow<List<AttendanceRecord>> {
        val result = mutableListOf<AttendanceRecord>()
        val dummyMap = dummyRecords

        var currentDate = start
        while (!currentDate.isAfter(end)) {
            if (currentDate.year == 2026 && currentDate.monthValue == 9) {
                dummyMap[currentDate]?.let { record ->
                    result.add(record.copy(monthOfYear = 9, year = 2026))
                }
            } else if (currentDate.dayOfWeek == DayOfWeek.SATURDAY || currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
                result.add(
                    AttendanceRecord(
                        id = "${currentDate.year}_${currentDate.monthValue}_${currentDate.dayOfMonth}",
                        dayOfMonth = currentDate.dayOfMonth,
                        dayOfWeek = currentDate.dayOfWeek.name,
                        status = AttendanceStatus.Holiday,
                        monthOfYear = currentDate.monthValue,
                        year = currentDate.year
                    )
                )
            }
            currentDate = currentDate.plusDays(1)
        }
        return flowOf(result)
    }

    override fun observeUserAttendanceStatusReport(
        year: Int,
        attendanceStatus: AttendanceStatus
    ): Flow<Int> {
        return flowOf(
            if (year != 2026) 0
            else
                when (attendanceStatus) {
                    AttendanceStatus.Present -> 12
                    AttendanceStatus.Absent -> 2
                    AttendanceStatus.Leave -> 3
                    AttendanceStatus.Pending, AttendanceStatus.InProgress -> 1
                    else -> 0
                }
        )
    }

    override suspend fun getAttendanceRapport(day: LocalDate): AttendanceRecord? {
        return dummyRecords[day]
    }

    override suspend fun cancelDayOffRequest(day: LocalDate) {

    }
}
