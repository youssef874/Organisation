package com.example.orgcore.api.attendance.model

data class OrgAttendance(
    val id: Long,
    val date: Long,
    val checkOut: Long,
    val checkIn: Long,
    val attendanceStatus: @AttendanceStatus String,
    val accountId: Long,
    /**
     * True if the request was handled successfully by the server but the supervisor has not accepted or rejected yet or queued, otherwise false.
     */
    val isRequestInProgress: Boolean = false
)
