package com.example.orgcore.api.attendance.event

import androidx.annotation.StringDef

/**
 * Defines the types of attendance-related events dispatched across the SDK.
 */
@StringDef(
    AttendanceEventType.ATTENDANCE_STATUS_UPDATE_SUCCESS,
    AttendanceEventType.ATTENDANCE_STATUS_UPDATE_FAILURE
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceEventType {
    companion object {
        /** Event type emitted when an attendance status update succeeds. */
        const val ATTENDANCE_STATUS_UPDATE_SUCCESS = "attendance_status_update_success"
        /** Event type emitted when an attendance status update fails. */
        const val ATTENDANCE_STATUS_UPDATE_FAILURE = "attendance_status_update_failure"
    }
}
