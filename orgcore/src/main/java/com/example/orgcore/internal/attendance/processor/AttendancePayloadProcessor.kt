package com.example.orgcore.internal.attendance.processor

import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgcore.internal.commun.request.ServerRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class AttendancePayloadProcessor(
    private val attendanceUpdatePayloadProcessor: PayloadProcessor<Attendance>
) : PayloadProcessor<Any> {

    companion object {
        private const val KEY_BUSINESS_TYPE = "businessType"
    }

    private var activeProcessor: PayloadProcessor<*>? = null

    override suspend fun decode(json: JSONObject) {
        withContext(Dispatchers.Default) {
            val businessType = json.optString(KEY_BUSINESS_TYPE)

            activeProcessor = when (businessType) {
                AttendanceBusinessType.ATTENDANCE_UPDATE -> {
                    attendanceUpdatePayloadProcessor.apply {
                        decode(json)
                    }
                }
                else -> null
            }
        }
    }

    override fun getServerRequest(): ServerRequest<Any> {
        @Suppress("UNCHECKED_CAST")
        return activeProcessor?.getServerRequest() as? ServerRequest<Any>
            ?: throw IllegalStateException("Unknown or unsupported attendance business type")
    }

    override fun getCache(): OfflineCache<Any>? {
        @Suppress("UNCHECKED_CAST")
        return activeProcessor?.getCache() as? OfflineCache<Any>
    }
}
