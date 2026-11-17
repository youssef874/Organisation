package com.example.orgcore.internal.commun

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgcore.internal.commun.model.RequestResult
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.model.DBOfflineRequest
import java.util.UUID

/**
 * Fluent builder for executing remote server requests with connectivity checks,
 * offline queuing via request payloads, and local caching.
 */
internal class RequestExecutionBuilder<T>(
    private val request: ServerRequest<T>
) {
    companion object {
        private const val TAG = "REQUEST"
        private const val CLASS_NAME = "RequestExecutionBuilder"
    }
    private var requestType: String? = null
    private var priority: Int = RequestPriority.PRIORITY_NORMAL
    private var allowOfflineQueue: Boolean = true
    private var cache: OfflineCache<T>? = null

    fun requestType(type: String) = apply { this.requestType = type }
    fun priority(priority: Int) = apply { this.priority = priority }
    fun allowOfflineQueue(allow: Boolean) = apply { this.allowOfflineQueue = allow }

    /**
     * Optional local cache implementation (can be Room DB, DataStore, SharedPreferences, RAM, etc.).
     * If omitted, data is passed straight to Success upon completion.
     */
    fun cache(cache: OfflineCache<T>?) = apply { this.cache = cache }

    suspend fun execute(
        networkMonitor: NetworkMonitor,
        offlineQueueCache: OfflineCache<DBOfflineRequest>
    ): RequestResult<T> {
        if (!networkMonitor.isConnected()) {
            if (allowOfflineQueue && requestType != null) {
                val uuid = UUID.randomUUID().toString()
                request.setRequestUuid(uuid)
                val dbOfflineRequest = createOfflineCache(
                    requestUuid = uuid,
                    priority = priority,
                    requestType = requestType!!,
                    payload = request.payload()
                )
                val cached = offlineQueueCache.cache(dbOfflineRequest)
                return if (cached) {
                    RequestResult.OfflineQueued
                } else {
                    RequestResult.CacheFailure
                }
            } else {
                return RequestResult.ConnectionLost
            }
        }

        return try {
            when (val result = request.request()) {
                is NetworkResult.Success -> {
                    val cacheResult = cache?.cache(result.data) ?: true
                    if (cacheResult) {
                        RequestResult.Success(result.data)
                    } else {
                        RequestResult.CacheFailure
                    }
                }
                is NetworkResult.Error -> {
                    RequestResult.ServerFailure(result.code)
                }
            }
        } catch (e: Throwable) {
            LoggerHelper.logger.e(e, TAG, "$CLASS_NAME|execute| Exception thrown during request execution")
            RequestResult.ServerFailure(-1)
        }
    }
}
