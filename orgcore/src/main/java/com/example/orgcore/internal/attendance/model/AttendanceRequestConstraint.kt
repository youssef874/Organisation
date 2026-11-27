package com.example.orgcore.internal.attendance.model

import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgcore.internal.commun.model.RequestType

internal abstract class AttendanceRequestConstraints(
    override val requestType: @RequestType String = RequestType.ATTENDANCE,
    val serverAccountId: String
): RequestConstraint

internal data class CancelAttendanceRequestConstraints(
    override val businessType: String = AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST,
    override val priority: Int = RequestPriority.PRIORITY_HIGH,
    val accountId: String,
    val date: Long,
): AttendanceRequestConstraints(
    serverAccountId = accountId
)
