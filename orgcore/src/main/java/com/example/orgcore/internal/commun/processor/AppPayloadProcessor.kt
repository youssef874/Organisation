package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgdatabase.public.model.OfflineRequestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class AppPayloadProcessor(
    private val attendancePayloadProcessor: ()->PayloadProcessor<RequestConstraint>
) : PayloadProcessor<RequestConstraint> {

    companion object {
        const val KEY_REQUEST_TYPE = "requestType"
    }
    override suspend fun decode(json: JSONObject): RequestConstraint {
        return withContext(Dispatchers.Default) {
            val requestType = json.optString(KEY_REQUEST_TYPE)

            return@withContext when (requestType) {
                OfflineRequestType.ATTENDANCE -> attendancePayloadProcessor().decode(json)
                else -> throw IllegalStateException("Unknown or unsupported request type: $requestType")
            }
        }
    }
}
