package com.example.orgcore.api.attendance.model

data class OrgAttendance(
    val id: Long,
    val date: Long,
    val checkOut: Long,
    val checkIn: Long,
    val attendanceStatus: @AttendanceStatus String,
    val accountId: Long
)
