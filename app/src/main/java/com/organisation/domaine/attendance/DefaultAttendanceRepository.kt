package com.organisation.domaine.attendance

import com.example.orgcore.api.attendance.AttendanceApi
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class DefaultAttendanceRepository(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) : AttendanceRepository {

    override fun observeMonthlyAttendanceReport(
        month: Int,
        year: Int,
    ): Flow<List<AttendanceRecord>> {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = endDate.atTime(23, 59, 59, 999_999_999).atZone(zoneId).toInstant().toEpochMilli()

        return AttendanceApi.observeUserAttendanceRapportByRange(startMillis, endMillis).map { list ->
            list.toAttendanceRecords(zoneId)
        }
    }

    override fun observeUserAttendanceReportViaRanger(
        start: LocalDate,
        end: LocalDate,
    ): Flow<List<AttendanceRecord>> {
        val startMillis = start.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = end.atTime(23, 59, 59, 999_999_999).atZone(zoneId).toInstant().toEpochMilli()

        return AttendanceApi.observeUserAttendanceRapportByRange(startMillis, endMillis).map { list ->
            list.toAttendanceRecords(zoneId)
        }
    }

    override fun observeUserAttendanceStatusReport(
        year: Int,
        attendanceStatus: AttendanceStatus,
    ): Flow<Int> {
        val startOfYear = LocalDate.of(year, 1, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfYear = LocalDate.of(year, 12, 31).atTime(23, 59, 59, 999_999_999).atZone(zoneId).toInstant().toEpochMilli()
        val coreStatus = attendanceStatus.toCoreAttendanceStatus()

        return AttendanceApi.observeUserAttendanceStatusCountForRange(
            startRange = startOfYear,
            endRange = endOfYear,
            attendanceStatus = coreStatus,
        )
    }

    override suspend fun getAttendanceRapport(day: LocalDate): AttendanceRecord? {
        val dayMillis = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
        return AttendanceApi.getUserAttendanceRapportForDay(dayMillis)?.toAttendanceRecord(zoneId)
    }

    override suspend fun cancelDayOffRequest(day: LocalDate) {
        // TODO
    }
}
