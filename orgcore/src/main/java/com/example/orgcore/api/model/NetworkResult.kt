package com.example.orgcore.api.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
internal sealed class NetworkResult<out T> {

    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Error(
        val code: Int,
        val message: String,
        val exception: Throwable? = null
    ) : NetworkResult<Nothing>()
}
