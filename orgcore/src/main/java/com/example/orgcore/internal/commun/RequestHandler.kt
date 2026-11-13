package com.example.orgcore.internal.commun

/**
 * Interface representing a network or data request handler capable of orchestrating
 * request execution, offline queuing, and caching.
 */
internal interface RequestHandler {

    /**
     * Executes the request handling workflow.
     */
    suspend fun handleRequest()
}
