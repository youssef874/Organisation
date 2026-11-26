package com.example.orgcore.api.attendance.model

import androidx.annotation.StringDef
import com.example.orgdatabase.public.model.AttendanceStatusType

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
        const val ABSENT = AttendanceStatusType.ABSENT
        const val PRESENT = AttendanceStatusType.PRESENT
        const val IN_PROGRESS = AttendanceStatusType.IN_PROGRESS
        const val PENDING = AttendanceStatusType.PENDING
        const val HOLIDAY = AttendanceStatusType.HOLIDAY
        const val PERSONAL_HOLIDAY = AttendanceStatusType.PERSONAL_HOLIDAY
        const val WEEKEND = AttendanceStatusType.WEEKEND
        const val PAID_LEAVE = AttendanceStatusType.PAID_LEAVE
        const val UNPAID_LEAVE = AttendanceStatusType.UNPAID_LEAVE
        const val PAID_PARTIAL_LEAVE = AttendanceStatusType.PAID_PARTIAL_LEAVE
        const val UNPAID_PARTIAL_LEAVE = AttendanceStatusType.UNPAID_PARTIAL_LEAVE
        const val NONE = AttendanceStatusType.NONE
    }
}
