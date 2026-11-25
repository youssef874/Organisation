package com.example.orgcore.internal.attendance.model

import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgcore.internal.commun.model.RequestType
import com.example.orgdatabase.public.model.DBAttendance

internal data class AttendanceRequestConstraint(
    override val requestType: @RequestType String = RequestType.ATTENDANCE,
    override val businessType: @AttendanceBusinessType String,
    override val priority: Int = RequestPriority.PRIORITY_HIGH,
    val attendance: DBAttendance,
    val serverAccountId: String
) : RequestConstraint
