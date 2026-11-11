package com.example.orgcore.internal.attendance.mapper

import com.example.orgcore.api.attendance.model.OrgAttendance
import com.example.orgcore.internal.attendance.model.Attendance
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
