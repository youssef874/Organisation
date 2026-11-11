package com.example.orgcore.internal.attendance.model

import com.example.orgcore.api.attendance.model.AttendanceStatus
import java.util.UUID

data class Attendance(
    val id: String = UUID.randomUUID().toString(),
    val serverAccountId: String,
    val checkIn: Long,
    val checkOut: Long,
    val date: Long,
    val attendanceStatus: @AttendanceStatus String,
    val requestReason: String? = null,
    val isApproved: Boolean = false,
    val isRequestInProgress: Boolean = false
)
