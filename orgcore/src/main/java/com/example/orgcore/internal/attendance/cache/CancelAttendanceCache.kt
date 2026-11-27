package com.example.orgcore.internal.attendance.cache

import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi

internal class CancelAttendanceCache(
    private val accountDataApi: AccountDataApi
): OfflineCache<Attendance> {

    companion object{
        private const val CLASS_NAME = "CancelAttendanceCache"
        private const val TAG = "ATTENDANCE"
    }

    override suspend fun cache(data: Attendance): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|cache|attendanceId: ${data.id}, serverAccountId: ${data.serverAccountId}, date: ${data.date}"
        )

        val account = accountDataApi.getAccountByServerId(data.serverAccountId)
        if (account == null) {
            LoggerHelper.logger.w(TAG, "$CLASS_NAME|cache|Account not found for serverId: ${data.serverAccountId}")
            return false
        }

        val success = try {
            accountDataApi.updateAttendanceStatus(
                accountId = account.id,
                date = data.date,
                status = data.attendanceStatus,
                isRequestInProgress = true
            )
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME|cache|Error cancelling attendance in database: ${e.message}")
            false
        }

        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|cache|cancel attendance success: $success"
        )
        return success
    }
}
