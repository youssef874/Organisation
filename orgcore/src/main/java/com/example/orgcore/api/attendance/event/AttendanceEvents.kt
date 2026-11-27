package com.example.orgcore.api.attendance.event

import com.example.orgcore.api.model.OrgEvent

/**
 * Base class for all attendance-related domain events.
 *
 * @property accountId The ID of the account associated with the event.
 * @property date The date associated with the event.
 * @property type The type of the attendance event (see [AttendanceEventType]).
 */
abstract class AttendanceEvents(val accountId: Long, val date: Long, type: @AttendanceEventType String) :
    OrgEvent(type)

data class CancelAttendanceRequestTaken(
    val attendanceAccountId: Long,
    val day: Long
): AttendanceEvents(
    accountId = attendanceAccountId,
    date = day,
    type = AttendanceEventType.CANCEL_ATTENDANCE_REQUEST_TAKEN
)

data class CancelAttendanceDenied(
    val attendanceAccountId: Long,
    val day: Long,
    val failReason: @AttendanceRequestFailReason String
): AttendanceEvents(
    accountId = attendanceAccountId,
    date = day,
    type = AttendanceEventType.CANCEL_ATTENDANCE_REQUEST_REJECTED
)
