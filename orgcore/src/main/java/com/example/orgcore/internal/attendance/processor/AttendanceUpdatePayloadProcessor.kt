package com.example.orgcore.internal.attendance.processor

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.model.AttendanceRequestConstraint
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAttendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

internal class AttendanceUpdatePayloadProcessor(
    private val accountDataApi: AccountDataApi
): PayloadProcessor<AttendanceRequestConstraint> {

    companion object{
        private const val CLASS_NAME = "AttendanceUpdatePayloadProcessor"
        private const val TAG = "ATTENDANCE"
        const val KEY_ID = "id"
        const val KEY_ACCOUNT_ID = "accountId"
        const val KEY_CHECK_IN = "checkIn"
        const val KEY_CHECK_OUT = "checkOut"
        const val KEY_DATE = "date"
        const val KEY_ATTENDANCE_STATUS = "attendanceStatus"
        const val KEY_REQUEST_REASON = "requestReason"
        const val KEY_IS_REQUEST_IN_PROGRESS = "isRequestInProgress"
    }

    override suspend fun decode(json: JSONObject): AttendanceRequestConstraint {
        return withContext(Dispatchers.IO) {
            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|start decoding attendance update payload")
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

            val account = accountDataApi.getAccountByServerId(serverAccountId)
            require(account != null) { "Account not found for serverId: $serverAccountId" }
            val accountId = account.id

            val dbAttendance = DBAttendance(
                accountId = accountId,
                serverId = id,
                checkIn = checkIn,
                checkOut = checkOut,
                date = date,
                attendanceStatus = attendanceStatus,
                requestReason = requestReason,
                isRequestInProgress = isRequestInProgress
            )

            LoggerHelper.logger.i(TAG, "$CLASS_NAME|decode|successfully decoded attendance id: $id, accountId: $serverAccountId")

            AttendanceRequestConstraint(
                businessType = AttendanceBusinessType.ATTENDANCE_UPDATE,
                attendance = dbAttendance,
                serverAccountId = serverAccountId
            )
        }
    }
}
