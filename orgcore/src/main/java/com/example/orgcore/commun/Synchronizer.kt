package com.example.orgcore.commun

/**
 * Orchestrates the synchronization flow, typically coordinating between a [ServerRequest]
 * and an [OfflineCache].
 */
internal interface Synchronizer {

    /**
     * Triggers the synchronization process.
     *
     * @return A [SyncResult] representing the outcome of the synchronization operation.
     */
    suspend fun synchronize(): SyncResult

    /**
     * Checks if the data managed by this synchronizer is currently synchronized.
     *
     * @return `true` if data is considered up-to-date and valid locally, `false` otherwise.
     */
    suspend fun isSynchronized(): Boolean
}