package com.example.orgcore.internal.attendance.model

import androidx.annotation.StringDef

/**
 * Defines the business types/actions for attendance-related network and offline requests.
 */
@StringDef(
    AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceBusinessType {
    companion object {


        /** Business type for cancelling an attendance request. */
        const val CANCEL_ATTENDANCE_REQUEST = "cancel_attendance_request"
    }
}
