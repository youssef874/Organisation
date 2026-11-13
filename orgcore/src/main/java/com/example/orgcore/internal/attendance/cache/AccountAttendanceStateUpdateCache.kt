package com.example.orgcore.internal.attendance.cache

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.mapper.toDbStatus
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi
import java.text.DateFormat
import java.util.Date

internal class AccountAttendanceStateUpdateCache(
    private val accountId: Long,
    private val accountDataApi: AccountDataApi,
    private val date: Long
): OfflineCache<@AttendanceStatus String> {

    companion object{
        private const val CLASS_NAME = "AccountAttendanceStateUpdateCache"
        private const val TAG = "ATTENDANCE"
    }

    override suspend fun cache(data: @AttendanceStatus String): Boolean {
        val day = DateFormat.getDateInstance().format(Date(date))
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|cache| new status: $data, date: $day, accountId: $accountId"
        )
        return accountDataApi.updateAttendanceStatus(
            accountId = accountId,
            status = data.toDbStatus(),
            date = date
        )
    }
}