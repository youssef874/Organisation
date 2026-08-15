package com.example.orgcore.commun

/**
 * Abstraction for persisting data in a local/offline storage.
 *
 * @param T The type of data to be cached.
 */
internal interface OfflineCache<T> {

    /**
     * Persists the provided [data] to the local store.
     *
     * @param data The data object to be cached.
     * @return `true` if the caching operation was successful, `false` otherwise.
     */
    suspend fun cache(data: T): Boolean
}