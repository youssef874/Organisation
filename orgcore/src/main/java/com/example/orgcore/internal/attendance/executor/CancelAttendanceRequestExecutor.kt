package com.example.orgcore.internal.attendance.executor

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.attendance.mapper.toAttendance
import com.example.orgcore.internal.attendance.mapper.toDBAttendance
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.CancelAttendanceRequestConstraints
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.executor.RequestExecutor
import com.example.orgcore.internal.commun.model.RequestResult
import com.example.orgcore.internal.commun.model.ServerRequestErrorType
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAttendance
import com.example.orgdatabase.public.model.DBOfflineRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

internal class CancelAttendanceRequestExecutor(
    private val networkMonitor: NetworkMonitor,
    private val accountDataApi: AccountDataApi,
    private val offlineCache: OfflineCache<DBOfflineRequest>,
    private val serverRequest: (CancelAttendanceRequestConstraints)->ServerRequest<Attendance>,
    private val cache: (CancelAttendanceRequestConstraints)->OfflineCache<Attendance>
): RequestExecutor<DBAttendance, CancelAttendanceRequestConstraints> {

    companion object {
        private const val CLASS_NAME = "CancelAttendanceRequestExecutor"
        private const val TAG = "ATTENDANCE"
    }

    override suspend fun execute(constraint: CancelAttendanceRequestConstraints): RequestResult<DBAttendance> {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|execute|constraint: $constraint"
        )
        val request = serverRequest(constraint)
        val cancelAttendanceHandler = cache(constraint)
        return withContext(Dispatchers.IO){
            require(constraint.date > 0L) { "Invalid or missing date: ${constraint.date}" }
            require(constraint.accountId.isNotBlank()) { "Invalid or missing accountId: ${constraint.accountId}" }

            val account = accountDataApi.getAccountByServerId(constraint.accountId)
                ?: return@withContext RequestResult.CacheFailure

            if (!networkMonitor.isConnected()){
                LoggerHelper.logger.w(TAG, "$CLASS_NAME|execute|network disconnected, offline queueing request")
                val uuid = UUID.randomUUID().toString()
                request.setRequestUuid(uuid)
                val dbRequest = DBOfflineRequest(
                    requestUuid = uuid,
                    priority = constraint.priority,
                    requestType = constraint.requestType,
                    payloadJson = request.payload(),
                    createdAt = System.currentTimeMillis()
                )
                try {
                    val cached = offlineCache.cache(dbRequest)
                    if (cached){
                        LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|offline request successfully queued, updating local cache")
                        val attendance = accountDataApi
                            .getAttendanceByDateAndAccountServerId(date = constraint.date, accountServerId = constraint.serverAccountId)
                            ?: return@withContext RequestResult.CacheFailure
                        val cacheResult = cancelAttendanceHandler.cache(
                            attendance.toAttendance(
                                constraint.serverAccountId
                            )
                        )
                        if (cacheResult) {
                            RequestResult.OfflineQueued
                        } else {
                            RequestResult.CacheFailure
                        }
                    }else{
                        LoggerHelper.logger.e(TAG, "$CLASS_NAME|execute|failed to cache offline request")
                        RequestResult.CacheFailure
                    }
                } catch (e: Throwable) {
                    LoggerHelper.logger.e(e, TAG, "$CLASS_NAME|execute|exception thrown during offline caching")
                    RequestResult.CacheFailure
                }
            }else{
                LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|network connected, executing server request")
                val networkResult = try {
                    request.request()
                } catch (e: Throwable) {
                    LoggerHelper.logger.e(e, TAG, "$CLASS_NAME|execute|exception thrown during server request")
                    return@withContext RequestResult.ServerFailure(ServerRequestErrorType.UNKNOWN_ERROR)
                }

                when(networkResult){
                    is NetworkResult.Success->{
                        LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|server request succeeded, updating cache")
                        try {
                            val cacheResult = cancelAttendanceHandler.cache(networkResult.data)
                            if (cacheResult) {
                                LoggerHelper.logger.i(TAG, "$CLASS_NAME|execute|cache update succeeded")
                                val dbAttendance = accountDataApi.getAttendanceByDate(account.id, constraint.date)
                                    ?: networkResult.data.toDBAttendance(account.id)
                                RequestResult.Success(dbAttendance)
                            } else {
                                LoggerHelper.logger.e(TAG, "$CLASS_NAME|execute|cache update failed")
                                RequestResult.CacheFailure
                            }
                        } catch (e: Throwable) {
                            LoggerHelper.logger.e(e, TAG, "$CLASS_NAME|execute|exception thrown during local cache update")
                            RequestResult.CacheFailure
                        }
                    }
                    is NetworkResult.Error -> {
                        LoggerHelper.logger.e(TAG, "$CLASS_NAME|execute|server request failed with error code: ${networkResult.code}")
                        RequestResult.ServerFailure(networkResult.code)
                    }
                }
            }
        }
    }
}
