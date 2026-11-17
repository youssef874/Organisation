package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.request.ServerRequest
import org.json.JSONObject

/**
 * Interface for processing request payloads, obtaining server requests, and retrieving associated caches.
 *
 * @param T The type of data expected by the request and cache.
 */
internal interface PayloadProcessor<T> {

    /**
     * Processes a pre-parsed [JSONObject] payload.
     * Each layer extracts only the metadata/fields it is responsible for.
     */
    suspend fun decode(json: JSONObject)

    /**
     * Returns the [ServerRequest] configured for execution.
     */
    fun getServerRequest(): ServerRequest<T>

    /**
     * Returns the [OfflineCache] implementation for persisting response data or state updates.
     */
    fun getCache(): OfflineCache<T>?
}
