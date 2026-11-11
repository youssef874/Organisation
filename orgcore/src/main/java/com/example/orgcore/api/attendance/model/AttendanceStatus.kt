package com.example.orgcore.api.attendance.model

import androidx.annotation.StringDef

@StringDef(
    AttendanceStatus.ABSENT,
    AttendanceStatus.PRESENT,
    AttendanceStatus.IN_PROGRESS,
    AttendanceStatus.PENDING,
    AttendanceStatus.HOLIDAY,
    AttendanceStatus.PERSONAL_HOLIDAY,
    AttendanceStatus.WEEKEND,
    AttendanceStatus.PAID_LEAVE,
    AttendanceStatus.UNPAID_LEAVE,
    AttendanceStatus.PAID_PARTIAL_LEAVE,
    AttendanceStatus.UNPAID_PARTIAL_LEAVE,
    AttendanceStatus.NONE
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceStatus {
    companion object {
        const val ABSENT = "absent"
        const val PRESENT = "present"
        const val IN_PROGRESS = "in_progress"
        const val PENDING = "pending"
        const val HOLIDAY = "holiday"
        const val PERSONAL_HOLIDAY = "personal holiday"
        const val WEEKEND = "weekend"
        const val PAID_LEAVE = "paid leave"
        const val UNPAID_LEAVE = "unpaid leave"
        const val PAID_PARTIAL_LEAVE = "paid partial leave"
        const val UNPAID_PARTIAL_LEAVE = "unpaid_partial_leave"
        const val NONE = "none"
    }
}
