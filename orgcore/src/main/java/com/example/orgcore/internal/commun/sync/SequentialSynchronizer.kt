package com.example.orgcore.internal.commun.sync

import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.internal.commun.LoggerHelper

/**
 * [Synchronizer] implementation that executes a list of synchronizers sequentially in order.
 *
 * It executes each provided synchronizer one after another. If any synchronizer
 * returns a [SyncResult.Error] or [SyncResult.PartialSuccess], execution halts immediately
 * and returns that result (strict success required to proceed).
 */
internal class SequentialSynchronizer(
    private val synchronizers: List<Synchronizer>
) : Synchronizer {

    companion object {
        private const val TAG = "SYNC"
        private const val CLASS_NAME = "SequentialSynchronizer"
    }

    override suspend fun synchronize(): SyncResult {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|synchronize|Starting sequential synchronization of ${synchronizers.size} synchronizer(s)")

        for ((index, synchronizer) in synchronizers.withIndex()) {
            LoggerHelper.logger.d(TAG, "$CLASS_NAME|synchronize|Executing synchronizer [$index] (${synchronizer::class.java.simpleName})")
            when (val result = synchronizer.synchronize()) {
                is SyncResult.Success -> {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME|synchronize|Synchronizer [$index] succeeded")
                }
                is SyncResult.PartialSuccess -> {
                    LoggerHelper.logger.w(TAG, "$CLASS_NAME|synchronize|Synchronizer [$index] completed with partial success: ${result.message}")
                    return result
                }
                is SyncResult.Error -> {
                    LoggerHelper.logger.e(TAG, "$CLASS_NAME|synchronize|Synchronizer [$index] failed with error: $result")
                    return result
                }
            }
        }

        LoggerHelper.logger.i(TAG, "$CLASS_NAME|synchronize|All sequential synchronizers completed successfully")
        return SyncResult.Success
    }

    override suspend fun isSynchronized(): Boolean {
        val synchronized = synchronizers.all { it.isSynchronized() }
        LoggerHelper.logger.d(TAG, "$CLASS_NAME|isSynchronized|Result: $synchronized")
        return synchronized
    }
}
