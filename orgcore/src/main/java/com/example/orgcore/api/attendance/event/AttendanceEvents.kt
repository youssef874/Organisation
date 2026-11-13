package com.example.orgcore.api.attendance.event

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.api.model.OrgEvent

/**
 * Base class for all attendance-related domain events.
 *
 * @property accountId The ID of the account associated with the event.
 * @property type The type of the attendance event (see [AttendanceEventType]).
 */
abstract class AttendanceEvents(val accountId: Long, type: @AttendanceEventType String) :
    OrgEvent(type)

/**
 * Event representing a successful attendance status update.
 *
 * @property attendanceAccountId The database ID of the account whose attendance was updated.
 * @property newState The new [AttendanceStatus] applied.
 */
data class AttendanceStatusUpdateSuccess(
    val attendanceAccountId: Long,
    val newState: @AttendanceStatus String
) : AttendanceEvents(attendanceAccountId, AttendanceEventType.ATTENDANCE_STATUS_UPDATE_SUCCESS)

/**
 * Event representing a failed attendance status update.
 *
 * @property attendanceAccountId The database ID of the account whose attendance update failed.
 * @property state The targeted [AttendanceStatus].
 * @property failReason The reason for failure (see [AttendanceRequestFailReason]).
 */
data class AttendanceStatusUpdateFailed(
    val attendanceAccountId: Long,
    val state: @AttendanceStatus String,
    val failReason: @AttendanceRequestFailReason String
) : AttendanceEvents(attendanceAccountId, AttendanceEventType.ATTENDANCE_STATUS_UPDATE_FAILURE)
