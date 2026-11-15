package com.example.orgcore.internal.commun

/**
 * Represents all possible exhaustive outcomes of a [RequestExecutionBuilder] execution pipeline.
 */
internal sealed class RequestResult<out T> {
    data class Success<T>(val data: T) : RequestResult<T>()
    data class ServerFailure(val errorCode: Int) : RequestResult<Nothing>()
    object ConnectionLost : RequestResult<Nothing>()
    object CacheFailure : RequestResult<Nothing>()
    object OfflineQueued : RequestResult<Nothing>()
}
