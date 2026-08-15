package com.example.orgcore.commun

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * [Synchronizer] implementation that aggregates multiple synchronizers.
 *
 * It executes all provided [synchronizers] in parallel using Coroutines and
 * returns a combined [SyncResult].
 *
 * @property synchronizers The list of synchronizers to be executed.
 */
internal class AccountCompositeSynchronizer(
    private val synchronizers: List<Synchronizer>
) : Synchronizer {

    override suspend fun synchronize(): SyncResult = coroutineScope {
        val results = synchronizers.map { async { it.synchronize() } }.awaitAll()
        println("test all: $results")

        val hasError = results.any { it is SyncResult.Error }
        val hasSuccess = results.any { it is SyncResult.Success || it is SyncResult.PartialSuccess }

        when {
            results.all { it is SyncResult.Success } -> SyncResult.Success
            hasError && hasSuccess -> SyncResult.PartialSuccess("Some synchronizers failed")
            hasError -> results.first { it is SyncResult.Error }
            else -> SyncResult.Success
        }
    }

    override suspend fun isSynchronized(): Boolean {
        return synchronizers.all { it.isSynchronized() }
    }
}
