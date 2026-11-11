package com.organisation.domaine.attendance

import com.example.orgcore.api.attendance.model.OrgAttendance
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.orgcore.api.attendance.model.AttendanceStatus as CoreAttendanceStatus

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Maps an [OrgAttendance] model to an [AttendanceRecord] domain model.
 */
fun OrgAttendance.toAttendanceRecord(
    zoneId: ZoneId = ZoneId.systemDefault(),
): AttendanceRecord {
    val instant = if (date > 10_000_000_000L) {
        Instant.ofEpochMilli(date)
    } else {
        Instant.ofEpochSecond(date)
    }
    val localDate = instant.atZone(zoneId).toLocalDate()

    return AttendanceRecord(
        id = id,
        dayOfMonth = localDate.dayOfMonth,
        dayOfWeek = localDate.dayOfWeek.name,
        checkIn = formatTimestampToTime(checkIn, zoneId),
        checkOut = formatTimestampToTime(checkOut, zoneId),
        status = attendanceStatus.toDomainAttendanceStatus(),
        requestReason = null,
        monthOfYear = localDate.monthValue,
        year = localDate.year,
    )
}

/**
 * Maps an [AttendanceRecord] domain model to an [OrgAttendance] model.
 */
fun AttendanceRecord.toOrgAttendance(
    accountId: Long = 0L,
    zoneId: ZoneId = ZoneId.systemDefault(),
): OrgAttendance {
    val dateMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    return OrgAttendance(
        id = id,
        date = dateMillis,
        checkIn = parseTimeToTimestamp(checkIn, date, zoneId),
        checkOut = parseTimeToTimestamp(checkOut, date, zoneId),
        attendanceStatus = status.toCoreAttendanceStatus(),
        accountId = accountId,
    )
}

/**
 * Maps a list of [OrgAttendance] models to a list of [AttendanceRecord] domain models.
 */
fun List<OrgAttendance>.toAttendanceRecords(
    zoneId: ZoneId = ZoneId.systemDefault(),
): List<AttendanceRecord> = map { it.toAttendanceRecord(zoneId) }

/**
 * Maps a list of [AttendanceRecord] domain models to a list of [OrgAttendance] models.
 */
fun List<AttendanceRecord>.toOrgAttendances(
    accountId: Long = 0L,
    zoneId: ZoneId = ZoneId.systemDefault(),
): List<OrgAttendance> = map { it.toOrgAttendance(accountId, zoneId) }

/**
 * Converts a raw attendance status string from core to the domain [AttendanceStatus] enum.
 */
fun String?.toDomainAttendanceStatus(): AttendanceStatus = when (this?.lowercase()?.trim()) {
    CoreAttendanceStatus.PRESENT -> AttendanceStatus.Present
    CoreAttendanceStatus.ABSENT -> AttendanceStatus.Absent
    CoreAttendanceStatus.IN_PROGRESS -> AttendanceStatus.InProgress
    CoreAttendanceStatus.PENDING -> AttendanceStatus.Pending
    CoreAttendanceStatus.HOLIDAY -> AttendanceStatus.Holiday
    CoreAttendanceStatus.PERSONAL_HOLIDAY -> AttendanceStatus.PersonalHoliday
    CoreAttendanceStatus.WEEKEND -> AttendanceStatus.Weekend
    CoreAttendanceStatus.PAID_LEAVE,
    CoreAttendanceStatus.UNPAID_LEAVE,
    -> AttendanceStatus.Leave
    CoreAttendanceStatus.PAID_PARTIAL_LEAVE,
    CoreAttendanceStatus.UNPAID_PARTIAL_LEAVE,
    -> AttendanceStatus.PartialLeave
    CoreAttendanceStatus.NONE -> AttendanceStatus.None
    else -> AttendanceStatus.None
}

/**
 * Converts a domain [AttendanceStatus] enum to the core attendance status string.
 */
fun AttendanceStatus.toCoreAttendanceStatus(): String = when (this) {
    AttendanceStatus.Present -> CoreAttendanceStatus.PRESENT
    AttendanceStatus.Absent -> CoreAttendanceStatus.ABSENT
    AttendanceStatus.Leave -> CoreAttendanceStatus.PAID_LEAVE
    AttendanceStatus.PartialLeave -> CoreAttendanceStatus.PAID_PARTIAL_LEAVE
    AttendanceStatus.Pending -> CoreAttendanceStatus.PENDING
    AttendanceStatus.Holiday -> CoreAttendanceStatus.HOLIDAY
    AttendanceStatus.PersonalHoliday -> CoreAttendanceStatus.PERSONAL_HOLIDAY
    AttendanceStatus.InProgress -> CoreAttendanceStatus.IN_PROGRESS
    AttendanceStatus.Weekend -> CoreAttendanceStatus.WEEKEND
    AttendanceStatus.None -> CoreAttendanceStatus.NONE
}

private fun formatTimestampToTime(timestamp: Long, zoneId: ZoneId): String? {
    if (timestamp <= 0L) return null
    val instant = if (timestamp > 10_000_000_000L) {
        Instant.ofEpochMilli(timestamp)
    } else {
        Instant.ofEpochSecond(timestamp)
    }
    return timeFormatter.withZone(zoneId).format(instant)
}

private fun parseTimeToTimestamp(timeStr: String?, date: LocalDate, zoneId: ZoneId): Long {
    if (timeStr.isNullOrBlank()) return 0L
    timeStr.toLongOrNull()?.let { return it }
    return try {
        val localTime = LocalTime.parse(timeStr, timeFormatter)
        date.atTime(localTime).atZone(zoneId).toInstant().toEpochMilli()
    } catch (_: Exception) {
        0L
    }
}
