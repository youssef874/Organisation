package com.example.orgcore.internal.attendance.mapper

import com.example.orgcore.api.attendance.event.AttendanceRequestFailReason
import com.example.orgcore.api.attendance.model.OrgAttendance
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.commun.model.ServerErrorCode
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
        requestedStatus = requestedStatus
    )
}

internal fun DBAttendance.toAttendance(serverAccountId: String): Attendance {
    return Attendance(
        serverAccountId = serverAccountId,
        id = serverId ?: id.toString(),
        checkIn = checkIn,
        checkOut = checkOut,
        date = date,
        attendanceStatus = attendanceStatus,
        requestReason = requestReason,
        requestedStatus = requestedStatus
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
        accountId = accountId,
        requestedStatus = requestedStatus
    )
}


internal fun @ServerErrorCode Int.toAttendanceFailReason(): @AttendanceRequestFailReason String {
    return when (this) {
        ServerErrorCode.UNAUTHORIZED,
        ServerErrorCode.FORBIDDEN -> AttendanceRequestFailReason.UNAUTHORIZED

        ServerErrorCode.REQUEST_TIMEOUT,
        ServerErrorCode.GATEWAY_TIMEOUT -> AttendanceRequestFailReason.TIMEOUT

        ServerErrorCode.UNPROCESSABLE_ENTITY -> AttendanceRequestFailReason.SUPERVISOR_REJECT

        else -> AttendanceRequestFailReason.SERVER_ISSUE
    }
}
