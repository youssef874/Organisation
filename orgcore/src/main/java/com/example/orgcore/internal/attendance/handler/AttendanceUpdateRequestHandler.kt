package com.example.orgcore.internal.attendance.handler

import com.example.orgcore.api.attendance.event.AttendanceRequestFailReason
import com.example.orgcore.api.attendance.event.AttendanceStatusUpdateFailed
import com.example.orgcore.api.attendance.event.AttendanceStatusUpdateSuccess
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.cache.AccountAttendanceStateUpdateCache
import com.example.orgcore.internal.attendance.mapper.toAttendanceFailReason
import com.example.orgcore.internal.attendance.request.FakerRequestAccountAttendanceStatusUpdate
import com.example.orgcore.internal.commun.BaseRequestHandler
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.ServerRequest
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.cache.OfflineQueueCache
import com.example.orgcore.internal.commun.createOfflineCache
import com.example.orgcore.internal.commun.event.OrgEventNotifier
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.api.OfflineRequestDataApi
import com.example.orgdatabase.public.model.DBOfflineRequest
import com.example.orgdatabase.public.model.OfflineRequestType

internal class AttendanceUpdateRequestHandler(
    private val accountId: Long,
    private val status: @AttendanceStatus String,
    private val eventNotifier: OrgEventNotifier? = null,
    date: Long,
    networkMonitor: NetworkMonitor,
    offlineRequestDataApi: OfflineRequestDataApi,
    accountDataApi: AccountDataApi,
    offlineQueueCache: OfflineQueueCache = OfflineQueueCache(
        offlineRequestDataApi
    ),
    request: ServerRequest<@AttendanceStatus String> = FakerRequestAccountAttendanceStatusUpdate(
        newStatus = status
    ),
    cache: OfflineCache<@AttendanceStatus String> = AccountAttendanceStateUpdateCache(
            accountId = accountId,
        accountDataApi = accountDataApi,
        date = date
    ),
): BaseRequestHandler<@AttendanceStatus String>(
    networkMonitor,
    offlineQueueCache,
    request,
    cache
) {
    override val dbOfflineRequest: DBOfflineRequest =
        createOfflineCache(
            priority = RequestPriority.PRIORITY_HIGH,
            requestType = OfflineRequestType.ATTENDANCE,
            payload = "accountId=$accountId,status=$status"
        )
    override val className: String = "AttendanceUpdateRequestHandler"
    override val tag: String = "ATTENDANCE"

    override fun onConnectionLost() {
        LoggerHelper.logger.w(tag, "$className|onConnectionLost| Connection lost or offline for accountId: $accountId")
        eventNotifier?.notify(
            AttendanceStatusUpdateFailed(
                attendanceAccountId = accountId,
                state = status,
                failReason = AttendanceRequestFailReason.NO_CONNECTION
            )
        )
    }

    override fun onServerRequestFails(errorCode: Int) {
        LoggerHelper.logger.e(tag, "$className|onServerRequestFails| Server request failed with error code: $errorCode for accountId: $accountId")
        val failReason = errorCode.toAttendanceFailReason()
        eventNotifier?.notify(
            AttendanceStatusUpdateFailed(
                attendanceAccountId = accountId,
                state = status,
                failReason = failReason
            )
        )
    }

    override fun onCacheSuccess() {
        LoggerHelper.logger.i(tag, "$className|onCacheSuccess| Attendance status successfully updated and cached for accountId: $accountId")
        eventNotifier?.notify(
            AttendanceStatusUpdateSuccess(
                attendanceAccountId = accountId,
                newState = status
            )
        )
    }

    override fun onCacheFailed() {
        LoggerHelper.logger.e(tag, "$className|onCacheFailed| Failed to cache attendance status for accountId: $accountId")
        eventNotifier?.notify(
            AttendanceStatusUpdateFailed(
                attendanceAccountId = accountId,
                state = status,
                failReason = AttendanceRequestFailReason.DATABASE_ERROR
            )
        )
    }
}
