package com.example.orgcore.internal.attendance.processor

import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.cache.AccountAttendanceStateUpdateCache
import com.example.orgcore.internal.attendance.request.FakerRequestAccountAttendanceStatusUpdate
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.api.AccountDataApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

internal class AttendanceUpdateStatePayloadProcessor(
    private val accountDataApi: AccountDataApi
) : PayloadProcessor<@AttendanceStatus String> {

    companion object {
        private const val KEY_REQUEST_UUID = "requestUuid"
        private const val KEY_ACCOUNT_ID = "accountId"
        private const val KEY_DATE = "date"
        private const val KEY_STATUS = "status"
    }

    private var decodedState: DecodedState? = null

    private data class DecodedState(
        val accountId: Long,
        val date: Long,
        val status: String,
        val requestUuid: String
    )

    override suspend fun decode(json: JSONObject) {
        withContext(Dispatchers.Default) {
            val accountId = json.optLong(KEY_ACCOUNT_ID, 0L)
            val date = json.optLong(KEY_DATE, 0L)
            require(accountId > 0L) { "Invalid or missing accountId: $accountId" }
            require(date > 0L) { "Invalid or missing date: $date" }

            decodedState = DecodedState(
                requestUuid = json.optString(KEY_REQUEST_UUID, UUID.randomUUID().toString()),
                accountId = accountId,
                date = date,
                status = json.optString(KEY_STATUS, AttendanceStatus.NONE)
            )
        }
    }

    override fun getServerRequest(): ServerRequest<@AttendanceStatus String> {
        val state = decodedState ?: throw IllegalStateException("decode must be called before getServerRequest")
        return FakerRequestAccountAttendanceStatusUpdate(
            newStatus = state.status,
            accountId = state.accountId,
            date = state.date,
            requestUuid = state.requestUuid
        )
    }

    override fun getCache(): OfflineCache<@AttendanceStatus String> {
        val state = decodedState ?: throw IllegalStateException("decode must be called before getCache")
        return AccountAttendanceStateUpdateCache(
            accountId = state.accountId,
            accountDataApi = accountDataApi,
            date = state.date
        )
    }
}
