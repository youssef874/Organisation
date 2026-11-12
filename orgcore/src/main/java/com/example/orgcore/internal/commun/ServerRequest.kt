package com.example.orgcore.internal.commun

import com.example.orgcore.api.model.NetworkResult

/**
 * Abstraction for fetching data from a remote server.
 *
 * @param T The type of data expected in the response.
 */
internal interface ServerRequest<T> {

    /**
     * Performs a network request to fetch data.
     *
     * @return A [NetworkResult] containing the data on success or error details on failure.
     */
    suspend fun request(): NetworkResult<T>
}