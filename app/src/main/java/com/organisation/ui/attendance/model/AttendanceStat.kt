package com.organisation.ui.attendance.model

import androidx.compose.ui.graphics.Color
import com.organisation.domaine.attendance.model.AttendanceStatus

data class AttendanceStat(
    val value: String,
    val label: String = "",
    val valueColor: Color = Color.Black,
    val attendanceStatus: AttendanceStatus = AttendanceStatus.None
)
