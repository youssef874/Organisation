package com.example.orgdatabase.internal.provider

import com.example.orgdatabase.public.model.DBOfflineRequest

/**
 * Interface for providing and managing offline request queue data from the local database.
 */
internal interface OfflineRequestProvider {

    /**
     * Adds a single offline request to the queue.
     *
     * @param request The offline request data to add.
     * @return `true` if successful, `false` otherwise.
     */
    suspend fun addRequest(request: DBOfflineRequest): Boolean

    /**
     * Adds a list of offline requests to the queue in a batch.
     *
     * @param requests The list of offline requests to add.
     * @return `true` if all requests were added successfully, `false` otherwise.
     */
    suspend fun addRequests(requests: List<DBOfflineRequest>): Boolean

    /**
     * Retrieves the next batch of offline requests to sync, sorted by priority (descending)
     * and creation time (ascending).
     *
     * @param limit The maximum number of requests to return.
     * @return A list of [DBOfflineRequest] items.
     */
    suspend fun getNextBatchToSync(limit: Int): List<DBOfflineRequest>

    /**
     * Retrieves the next batch of offline requests to sync filtered by request type,
     * sorted by priority (descending) and creation time (ascending).
     *
     * @param requestType The type of requests to filter by.
     * @param limit The maximum number of requests to return.
     * @return A list of [DBOfflineRequest] items.
     */
    suspend fun getNextBatchToSyncByType(requestType: String, limit: Int): List<DBOfflineRequest>

    /**
     * Retrieves a specific offline request by its unique UUID.
     *
     * @param requestUuid The UUID of the request.
     * @return The [DBOfflineRequest] if found, or `null` otherwise.
     */
    suspend fun getByRequestUuid(requestUuid: String): DBOfflineRequest?

    /**
     * Deletes a specific offline request by its unique UUID.
     *
     * @param requestUuid The UUID of the request to delete.
     * @return `true` if the request was found and deleted, `false` otherwise.
     */
    suspend fun deleteByRequestUuid(requestUuid: String): Boolean

    /**
     * Retrieves the total count of pending offline requests in the queue.
     *
     * @return The total count.
     */
    suspend fun getCount(): Int

    /**
     * Deletes all offline requests from the queue.
     *
     * @return `true` if successful, `false` otherwise.
     */
    suspend fun deleteAll(): Boolean
}
