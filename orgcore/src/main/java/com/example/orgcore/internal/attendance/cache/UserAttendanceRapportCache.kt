package com.example.orgcore.internal.attendance.cache

import com.example.orgcore.internal.attendance.mapper.toDBAttendance
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi

internal class UserAttendanceRapportCache(
    private val accountDataApi: AccountDataApi
): OfflineCache<List<Attendance>> {

    companion object{
        private const val TAG = "ATTENDANCE"
        private const val CLASS_NAME = "AttendanceRapportCache"
    }

    override suspend fun cache(data: List<Attendance>): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|cache|data size: ${data.size}"
        )

        val dbAttendances = data.mapNotNull { attendance ->
            val account = accountDataApi.getAccountByServerId(attendance.serverAccountId)
            if (account == null) {
                LoggerHelper.logger.w(TAG, "$CLASS_NAME|cache|Account not found for serverId: ${attendance.serverAccountId}")
                return@mapNotNull null
            }
            attendance.toDBAttendance(account.id)
        }

        return accountDataApi.addAttendances(dbAttendances)
    }
}
