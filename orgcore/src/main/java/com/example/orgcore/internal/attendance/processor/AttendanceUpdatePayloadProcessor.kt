package com.example.orgcore.internal.attendance.processor

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.cache.AttendanceUpdateCache
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.request.FakeAttendanceUpdatesServerRequest
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.api.AccountDataApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

internal class AttendanceUpdatePayloadProcessor(
    private val accountDataApi: AccountDataApi
): PayloadProcessor<Attendance> {

    companion object{
        private const val CLASS_NAME = "AttendanceUpdatePayloadProcessor"
        private const val TAG = "ATTENDANCE"
        const val KEY_REQUEST_UUID = "requestUuid"
        const val KEY_ID = "id"
        const val KEY_ACCOUNT_ID = "accountId"
        const val KEY_CHECK_IN = "checkIn"
        const val KEY_CHECK_OUT = "checkOut"
        const val KEY_DATE = "date"
        const val KEY_ATTENDANCE_STATUS = "attendanceStatus"
        const val KEY_REQUEST_REASON = "requestReason"
        const val KEY_IS_REQUEST_IN_PROGRESS = "isRequestInProgress"
    }

    private var decodedAttendance: DecodedAttendance? = null

    private data class DecodedAttendance(
        val requestUuid: String,
        val attendance: Attendance
    )

    override suspend fun decode(json: JSONObject) {
        withContext(Dispatchers.Default) {
            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|start decoding attendance update payload")
            val requestUuid = json.optString(KEY_REQUEST_UUID, UUID.randomUUID().toString())
            val id = json.optString(KEY_ID, UUID.randomUUID().toString())
            val serverAccountId = json.optString(KEY_ACCOUNT_ID, "")
            val checkIn = json.optLong(KEY_CHECK_IN, 0L)
            val checkOut = json.optLong(KEY_CHECK_OUT, 0L)
            val date = json.optLong(KEY_DATE, 0L)
            val attendanceStatus = json.optString(KEY_ATTENDANCE_STATUS, AttendanceStatus.NONE)
            val requestReason = if (json.has(KEY_REQUEST_REASON) && !json.isNull(KEY_REQUEST_REASON)) {
                json.optString(KEY_REQUEST_REASON)
            } else {
                null
            }
            val isRequestInProgress = json.optBoolean(KEY_IS_REQUEST_IN_PROGRESS, false)

            require(serverAccountId.isNotBlank()) { "Invalid or missing serverAccountId in attendance update payload" }
            require(date > 0L) { "Invalid or missing date in attendance update payload: $date" }

            val attendance = Attendance(
                id = id,
                serverAccountId = serverAccountId,
                checkIn = checkIn,
                checkOut = checkOut,
                date = date,
                attendanceStatus = attendanceStatus,
                requestReason = requestReason,
                isRequestInProgress = isRequestInProgress
            )

            decodedAttendance = DecodedAttendance(
                requestUuid = requestUuid,
                attendance = attendance
            )
            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|successfully decoded attendance id: $id, accountId: $serverAccountId")
        }
    }

    override fun getServerRequest(): ServerRequest<Attendance> {
        val decoded = decodedAttendance ?: throw IllegalStateException("decode must be called before getServerRequest")
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getServerRequest|supplying FakeAttendanceUpdatesServerRequest")
        return FakeAttendanceUpdatesServerRequest(
            requestUuid = decoded.requestUuid,
            updatedAttendance = decoded.attendance
        )
    }

    override fun getCache(): OfflineCache<Attendance> {
        decodedAttendance ?: throw IllegalStateException("decode must be called before getCache")
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getCache|supplying AttendanceUpdateCache")
        return AttendanceUpdateCache(accountDataApi)
    }
}
