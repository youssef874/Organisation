package com.example.orgcore.commun.model

/**
 * Represents the result of a synchronization operation.
 */
sealed class SyncResult {
    /**
     * Sync completed successfully.
     */
    data object Success : SyncResult()

    /**
     * Some parts of the sync succeeded, but others failed.
     */
    data class PartialSuccess(val message: String) : SyncResult()

    /**
     * The entire sync operation failed.
     */
    sealed class Error : SyncResult() {
        data class NetworkError(val exception: Throwable? = null) : Error()
        data class DatabaseError(val exception: Throwable? = null) : Error()
        data class Unauthorized(val message: String? = null) : Error()
        data class Unknown(val exception: Throwable? = null) : Error()
    }
}
