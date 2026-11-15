package com.example.orgcore.internal.attendance

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.cache.AccountAttendanceStateUpdateCache
import com.example.orgcore.internal.attendance.request.FakerRequestAccountAttendanceStatusUpdate
import com.example.orgcore.internal.commun.RequestExecutionBuilder
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.OfflineRequestType

internal class AttendanceRequestExecutionBuilderFactory(
    private val accountDataApi: AccountDataApi
) {

    fun create(
        accountId: Long,
        date: Long,
        state: @AttendanceStatus String
    ) = RequestExecutionBuilder(
        request = FakerRequestAccountAttendanceStatusUpdate(
            newStatus = state,
            accountId = accountId
        )
    )
        .allowOfflineQueue(true)
        .requestType(OfflineRequestType.ATTENDANCE)
        .priority(RequestPriority.PRIORITY_HIGH)
        .cache(AccountAttendanceStateUpdateCache(accountId, accountDataApi, date))
}
