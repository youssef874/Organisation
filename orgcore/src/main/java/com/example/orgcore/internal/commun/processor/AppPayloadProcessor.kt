package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.model.OfflineRequestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

internal class AppPayloadProcessor(
    private val attendancePayloadProcessor: PayloadProcessor<*>
) : PayloadProcessor<Any> {

    companion object {
        private const val KEY_REQUEST_TYPE = "requestType"
    }

    private var activeProcessor: PayloadProcessor<*>? = null

    override suspend fun decode(json: JSONObject) {
        withContext(Dispatchers.Default) {
            val requestType = json.optString(KEY_REQUEST_TYPE)

            activeProcessor = when (requestType) {
                OfflineRequestType.ATTENDANCE -> {
                    attendancePayloadProcessor.apply {
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
            ?: throw IllegalStateException("Unknown or unsupported request type")
    }

    override fun getCache(): OfflineCache<Any>? {
        @Suppress("UNCHECKED_CAST")
        return activeProcessor?.getCache() as? OfflineCache<Any>
    }
}
