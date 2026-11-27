package com.example.orgcore.api.attendance.event

import androidx.annotation.StringDef

/**
 * Defines the types of attendance-related events dispatched across the SDK.
 */
@StringDef(
    AttendanceEventType.CANCEL_ATTENDANCE_REQUEST_TAKEN,
    AttendanceEventType.CANCEL_ATTENDANCE_REQUEST_REJECTED
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceEventType {
    companion object {
        const val CANCEL_ATTENDANCE_REQUEST_TAKEN = "cancel_attendance_request_taken"
        const val CANCEL_ATTENDANCE_REQUEST_REJECTED = "cancel_attendance_request_rejected"
    }
}
