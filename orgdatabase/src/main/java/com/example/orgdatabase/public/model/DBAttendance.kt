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
    AttendanceStatusType.HOLIDAY,
    AttendanceStatusType.PERSONAL_HOLIDAY,
    AttendanceStatusType.WEEKEND,
    AttendanceStatusType.PAIED_LEAVE,
    AttendanceStatusType.UNPAIED_LEAVE,
    AttendanceStatusType.PAIED_PARTIAL_LEAVE,
    AttendanceStatusType.UNPAIED_PARTIAL_LEAVE,
    AttendanceStatusType.NONE
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class AttendanceStatusType {
    companion object {
        const val ABSENT = "absent"
        const val PRESENT = "present"
        const val IN_PROGRESS = "inprogress"
        const val HOLIDAY = "holiday"
        const val PERSONAL_HOLIDAY = "personal holiday"
        const val WEEKEND = "weekend"
        const val PAIED_LEAVE = "paied leave"
        const val UNPAIED_LEAVE = "unpaied leave"
        const val PAIED_PARTIAL_LEAVE = "paied partial leave"
        const val UNPAIED_PARTIAL_LEAVE = "unpaied partioal leave"
        const val NONE = "none"
    }
}
