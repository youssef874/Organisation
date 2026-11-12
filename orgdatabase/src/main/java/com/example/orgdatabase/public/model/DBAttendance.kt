package com.example.orgdatabase.public.model

import androidx.annotation.StringDef

data class DBAttendance(
    val id: Long = 0,
    val accountId: Long,
    val serverId: String? = null,
    val checkIn: Long,
    val checkOut: Long,
    val date: Long,
    val attendanceStatus: @AttendanceStatusType String,
    val requestReason: String? = null,
    val isApproved: Boolean = false,
    val isRequestInProgress: Boolean = false
)

@StringDef(
    AttendanceStatusType.ABSENT,
    AttendanceStatusType.PRESENT,
    AttendanceStatusType.IN_PROGRESS,
    AttendanceStatusType.PENDING,
    AttendanceStatusType.HOLIDAY,
    AttendanceStatusType.PERSONAL_HOLIDAY,
    AttendanceStatusType.WEEKEND,
    AttendanceStatusType.PAID_LEAVE,
    AttendanceStatusType.UNPAID_LEAVE,
    AttendanceStatusType.PAID_PARTIAL_LEAVE,
    AttendanceStatusType.UNPAID_PARTIAL_LEAVE,
    AttendanceStatusType.NONE
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceStatusType {
    companion object {
        const val ABSENT = "absent"
        const val PRESENT = "present"
        const val IN_PROGRESS = "in progress"
        const val PENDING = "pending"
        const val HOLIDAY = "holiday"
        const val PERSONAL_HOLIDAY = "personal holiday"
        const val WEEKEND = "weekend"
        const val PAID_LEAVE = "paid leave"
        const val UNPAID_LEAVE = "unpaid leave"
        const val PAID_PARTIAL_LEAVE = "paid partial leave"
        const val UNPAID_PARTIAL_LEAVE = "unpaid partial leave"
        const val NONE = "none"
    }
}
