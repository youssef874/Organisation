package com.example.orgcore.internal.commun.processor

import com.example.orgcore.internal.commun.model.RequestConstraint
import org.json.JSONObject

/**
 * Interface for processing request payloads, obtaining server requests, and retrieving associated caches.
 *
 * @param T The type of data expected by the request and cache.
 */
internal interface PayloadProcessor<out T: RequestConstraint> {

    /**
     * Processes a pre-parsed [JSONObject] payload.
     * Each layer extracts only the metadata/fields it is responsible for.
     */
    suspend fun decode(json: JSONObject):T

}
