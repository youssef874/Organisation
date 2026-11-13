package com.example.orgcore.internal.commun

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.cache.OfflineQueueCache
import com.example.orgcore.internal.commun.model.ServerRequestErrorType
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.model.DBOfflineRequest

/**
 * Base class for request handlers that manages network connectivity checks, server request execution,
 * offline request queuing, local caching, and success/failure callbacks.
 *
 * @param T The type of data handled by the request.
 * @property networkMonitor Monitors network connectivity status.
 * @property offlineQueueCache API for managing offline request queues.
 * @property request The remote server request execution logic.
 * @property cache The local offline cache implementation.
 */
internal abstract class BaseRequestHandler<T>(
    private val networkMonitor: NetworkMonitor,
    private val offlineQueueCache: OfflineQueueCache,
    private val request: ServerRequest<T>,
    private val cache: OfflineCache<T>
) : RequestHandler {

    /** Optional offline request descriptor to queue if network is unavailable or fails. */
    protected abstract val dbOfflineRequest: DBOfflineRequest?

    /** The class name used for logging and diagnostics. */
    abstract val className: String

    /** The log tag used for diagnostics. */
    abstract val tag: String

    /**
     * Handles the complete request workflow: checks connectivity, falls back to offline queue if disconnected,
     * executes server request, handles server errors, and persists data to local cache upon success.
     */
    override suspend fun handleRequest() {
        LoggerHelper.logger.i(
            tag,
            "$className|handleRequest|"
        )
        if (!networkMonitor.isConnected()){
            LoggerHelper.logger.w(
                tag,
                "$className|the app not connected do you support offline queue ${dbOfflineRequest != null}"
            )
            dbOfflineRequest?.let {
                val cached = offlineQueueCache.cache(it)
                if (!cached) {
                    onCacheFailed()
                }
            }?:run {
                onConnectionLost()
            }
            return
        }
        val requestResult = request.request()
        LoggerHelper.logger.i(
            tag,
            "$className|handleRequest|requestResult: $requestResult"
        )
        when(requestResult){
            is NetworkResult.Error -> {
                when(requestResult.code){
                    ServerRequestErrorType.NO_NETWORK,
                    ServerRequestErrorType.SERVER_CONNECTION_ERROR,
                    ServerRequestErrorType.INTERNAL_SERVER_ERROR->{
                        dbOfflineRequest?.let {
                            val cached = offlineQueueCache.cache(it)
                            if (!cached) {
                                onCacheFailed()
                            }
                        }?:run {
                            onConnectionLost()
                        }
                    }else -> onServerRequestFails(requestResult.code)
                }
            }
            is NetworkResult.Success<*> -> {
                val cacheResult = cache.cache(requestResult.data as T)
                LoggerHelper.logger.i(
                    tag,
                    "$className|handleRequest|cacheResult: $cacheResult"
                )
                if (cacheResult){
                    onCacheSuccess()
                }else{
                    onCacheFailed()
                }
            }
        }
    }

    /**
     * Invoked when network connection is lost or unavailable and no offline queue request is supported.
     */
    protected abstract fun onConnectionLost()

    /**
     * Invoked when the server request fails with a non-connectivity error code.
     *
     * @param errorCode The server/HTTP error code.
     */
    protected abstract fun onServerRequestFails(errorCode: Int)

    /**
     * Invoked when data caching succeeds following a successful server request.
     */
    protected abstract fun onCacheSuccess()

    /**
     * Invoked when data caching fails despite a successful server response or offline queue persistence failure.
     */
    protected abstract fun onCacheFailed()
}
