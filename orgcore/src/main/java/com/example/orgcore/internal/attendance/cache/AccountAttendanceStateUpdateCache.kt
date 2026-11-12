package com.example.orgcore.internal.attendance.cache

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.attendance.mapper.toDbStatus
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi
import java.text.DateFormat
import java.util.Date

internal class AccountAttendanceStateUpdateCache(
    private val accountId: Long? = null,
    private val accountDataApi: AccountDataApi,
    private val authenticationProvider: AuthenticationProvider,
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
        val id = accountId?:run {
            authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData("")
                .takeIf { it.isNotEmpty() }?.let {
                    accountDataApi.getAccountByServerId(it)?.id
                }
        }
        return if (id == null){
            LoggerHelper.logger.w(TAG, "$CLASS_NAME|cache|Failed to resolve account id for attendance update")
            false
        }else{
            accountDataApi.updateAttendanceStatus(
                accountId = id,
                status = data.toDbStatus(),
                date = date
            )
        }
    }
}