package com.example.orgcore.internal.commun.sync

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.internal.commun.LoggerHelper
import kotlinx.coroutines.CancellationException

/**
 * Base class for synchronizers that handles common network and caching flow.
 *
 * It provides a standard implementation of the [synchronize] method that:
 * 1. Fetches data from the [com.example.orgcore.internal.commun.ServerRequest].
 * 2. Handles network success and errors.
 * 3. Delegates caching logic to [performCaching].
 * 4. Returns a [SyncResult].
 *
 * @param T The type of data being synchronized.
 * @property serverRequest The request used to fetch data from the server.
 */
internal abstract class BaseSynchronizer<T>(
    private val serverRequest: com.example.orgcore.internal.commun.ServerRequest<T>
) : Synchronizer {

    protected abstract val className: String
    protected abstract val tag: String

    /**
     * Updates the local synchronization state.
     *
     * @param isSynchronized The new synchronization state.
     */
    abstract suspend fun updateIsSynchronized(isSynchronized: Boolean)

    override suspend fun synchronize(): SyncResult {
        return try {
            LoggerHelper.logger.i(tag, "$className|synchronize|Start sync")
            
            when (val networkResult = serverRequest.request()) {
                is NetworkResult.Success -> {
                    val cached = performCaching(networkResult.data)
                    if (cached) {
                        LoggerHelper.logger.i(tag, "$className|synchronize|sync completed")
                        updateIsSynchronized(true)
                        SyncResult.Success
                    } else {
                        LoggerHelper.logger.e(tag, "$className|synchronize|Failed to cache data")
                        updateIsSynchronized(false)
                        SyncResult.Error.DatabaseError()
                    }
                }
                is NetworkResult.Error -> {
                    LoggerHelper.logger.e(tag, "$className|synchronize|Network Error: ${networkResult.message}")
                    updateIsSynchronized(false)
                    if (networkResult.code == 401) {
                        SyncResult.Error.Unauthorized(networkResult.message)
                    } else {
                        SyncResult.Error.NetworkError()
                    }
                }
            }
        }catch (e: CancellationException){
            throw e
        }
        catch (e: Exception) {
            LoggerHelper.logger.e(tag, "$className|synchronize|Unexpected Error: ${e.message}")
            updateIsSynchronized(false)
            SyncResult.Error.Unknown(e)
        }
    }

    /**
     * Logic to persist the data fetched from the server.
     */
    protected abstract suspend fun performCaching(data: T): Boolean
}
