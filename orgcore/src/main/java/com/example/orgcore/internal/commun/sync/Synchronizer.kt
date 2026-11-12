package com.example.orgcore.internal.commun.sync

import com.example.orgcore.api.model.SyncResult

/**
 * Orchestrates the synchronization flow, typically coordinating between a [com.example.orgcore.internal.commun.ServerRequest]
 * and an [com.example.orgcore.internal.commun.cache.OfflineCache].
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