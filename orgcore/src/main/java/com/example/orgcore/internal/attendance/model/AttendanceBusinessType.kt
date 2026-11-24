package com.example.orgcore.internal.attendance.model

import androidx.annotation.StringDef

/**
 * Defines the business types/actions for attendance-related network and offline requests.
 */
@StringDef(
    AttendanceBusinessType.ATTENDANCE_UPDATE
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceBusinessType {
    companion object {
        /** Business type for updating an attendance . */
        const val ATTENDANCE_UPDATE = "attendance_update"
    }
}
