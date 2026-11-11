package com.example.orgcore.commun

import com.example.orgcore.commun.model.NetworkResult

/**
 * Abstraction for fetching data from a remote server.
 *
 * @param T The type of data expected in the response.
 */
internal interface ServerRequest<T> {

    /**
     * Performs a network request to fetch data.
     *
     * @return A [com.example.orgcore.commun.model.NetworkResult] containing the data on success or error details on failure.
     */
    suspend fun request(): NetworkResult<T>
}