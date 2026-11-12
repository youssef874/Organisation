package com.example.orgcore.internal.attendance.mapper

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.api.attendance.model.OrgAttendance
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgdatabase.public.model.AttendanceStatusType
import com.example.orgdatabase.public.model.DBAttendance

/**
 * Maps an internal [Attendance] domain model to a public database [DBAttendance] model.
 *
 * @param accountId The resolved local database primary key ID of the account.
 */
internal fun Attendance.toDBAttendance(accountId: Long): DBAttendance {
    return DBAttendance(
        accountId = accountId,
        serverId = id,
        checkIn = checkIn,
        checkOut = checkOut,
        date = date,
        attendanceStatus = attendanceStatus,
        requestReason = requestReason,
        isApproved = isApproved,
        isRequestInProgress = isRequestInProgress
    )
}

/**
 * Maps a database [DBAttendance] model to a public API [OrgAttendance] model.
 */
fun DBAttendance.toOrgAttendance(): OrgAttendance {
    return OrgAttendance(
        id = id,
        date = date,
        checkOut = checkOut,
        checkIn = checkIn,
        attendanceStatus = attendanceStatus,
        accountId = accountId
    )
}

fun @AttendanceStatus String.toDbStatus(): @AttendanceStatusType String{
    return when(this){
        AttendanceStatus.ABSENT-> AttendanceStatusType.ABSENT
        AttendanceStatus.PRESENT -> AttendanceStatusType.PRESENT
        AttendanceStatus.IN_PROGRESS -> AttendanceStatusType.IN_PROGRESS
        AttendanceStatus.PENDING -> AttendanceStatusType.PENDING
        AttendanceStatus.HOLIDAY -> AttendanceStatusType.HOLIDAY
        AttendanceStatus.PERSONAL_HOLIDAY -> AttendanceStatusType.PERSONAL_HOLIDAY
        AttendanceStatus.WEEKEND -> AttendanceStatusType.WEEKEND
        AttendanceStatus.PAID_LEAVE -> AttendanceStatusType.PAID_LEAVE
        AttendanceStatus.UNPAID_LEAVE -> AttendanceStatusType.UNPAID_LEAVE
        AttendanceStatus.PAID_PARTIAL_LEAVE -> AttendanceStatusType.PAID_PARTIAL_LEAVE
        AttendanceStatus.UNPAID_PARTIAL_LEAVE -> AttendanceStatusType.UNPAID_PARTIAL_LEAVE
        else -> AttendanceStatusType.NONE
    }
}

fun @AttendanceStatusType String.toApiStatus(): @AttendanceStatus String {
    return when (this) {
        AttendanceStatusType.ABSENT -> AttendanceStatus.ABSENT
        AttendanceStatusType.PRESENT -> AttendanceStatus.PRESENT
        AttendanceStatusType.IN_PROGRESS -> AttendanceStatus.IN_PROGRESS
        AttendanceStatusType.PENDING -> AttendanceStatus.PENDING
        AttendanceStatusType.HOLIDAY -> AttendanceStatus.HOLIDAY
        AttendanceStatusType.PERSONAL_HOLIDAY -> AttendanceStatus.PERSONAL_HOLIDAY
        AttendanceStatusType.WEEKEND -> AttendanceStatus.WEEKEND
        AttendanceStatusType.PAID_LEAVE -> AttendanceStatus.PAID_LEAVE
        AttendanceStatusType.UNPAID_LEAVE -> AttendanceStatus.UNPAID_LEAVE
        AttendanceStatusType.PAID_PARTIAL_LEAVE -> AttendanceStatus.PAID_PARTIAL_LEAVE
        AttendanceStatusType.UNPAID_PARTIAL_LEAVE -> AttendanceStatus.UNPAID_PARTIAL_LEAVE
        else -> AttendanceStatus.NONE
    }
}
