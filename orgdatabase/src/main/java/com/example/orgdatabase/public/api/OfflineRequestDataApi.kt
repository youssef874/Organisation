package com.example.orgdatabase.public.api

import android.content.Context
import com.example.orgdatabase.internal.DataProvider
import com.example.orgdatabase.public.model.DBOfflineRequest
import com.example.orgdatabase.public.model.OfflineRequestType

/**
 * API to manage offline request queue operations.
 */
class OfflineRequestDataApi(private val context: Context) {

    /**
     * Adds a single offline request to the queue.
     *
     * @param request The [DBOfflineRequest] to be added.
     * @return `true` if added successfully, `false` otherwise.
     */
    suspend fun addRequest(request: DBOfflineRequest): Boolean =
        DataProvider.getOfflineRequestProvider(context).addRequest(request)

    /**
     * Adds a list of offline requests to the queue in a batch.
     *
     * @param requests The list of [DBOfflineRequest] items to be added.
     * @return `true` if all requests were added successfully, `false` otherwise.
     */
    suspend fun addRequests(requests: List<DBOfflineRequest>): Boolean =
        DataProvider.getOfflineRequestProvider(context).addRequests(requests)

    /**
     * Retrieves the next batch of offline requests to sync, sorted by priority (descending)
     * and creation time (ascending).
     *
     * @param limit The maximum number of requests to return.
     * @return A list of [DBOfflineRequest] items.
     */
    suspend fun getNextBatchToSync(limit: Int): List<DBOfflineRequest> =
        DataProvider.getOfflineRequestProvider(context).getNextBatchToSync(limit)

    /**
     * Retrieves the next batch of offline requests to sync filtered by request type,
     * sorted by priority (descending) and creation time (ascending).
     *
     * @param requestType The type of requests to filter by, restricted to valid values defined in [OfflineRequestType].
     * @param limit The maximum number of requests to return.
     * @return A list of [DBOfflineRequest] items.
     */
    suspend fun getNextBatchToSyncByType(@OfflineRequestType requestType: String, limit: Int): List<DBOfflineRequest> =
        DataProvider.getOfflineRequestProvider(context).getNextBatchToSyncByType(requestType, limit)

    /**
     * Retrieves a specific offline request by its unique UUID.
     *
     * @param requestUuid The UUID of the request.
     * @return The [DBOfflineRequest] if found, or `null` otherwise.
     */
    suspend fun getByRequestUuid(requestUuid: String): DBOfflineRequest? =
        DataProvider.getOfflineRequestProvider(context).getByRequestUuid(requestUuid)

    /**
     * Deletes a specific offline request by its unique UUID.
     *
     * @param requestUuid The UUID of the request to delete.
     * @return `true` if the request was found and deleted, `false` otherwise.
     */
    suspend fun deleteByRequestUuid(requestUuid: String): Boolean =
        DataProvider.getOfflineRequestProvider(context).deleteByRequestUuid(requestUuid)

    /**
     * Retrieves the total count of pending offline requests in the queue.
     *
     * @return The total count.
     */
    suspend fun getCount(): Int =
        DataProvider.getOfflineRequestProvider(context).getCount()

    /**
     * Deletes all offline requests from the queue.
     *
     * @return `true` if successful, `false` otherwise.
     */
    suspend fun deleteAll(): Boolean =
        DataProvider.getOfflineRequestProvider(context).deleteAll()
}
