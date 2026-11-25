package com.example.orgcore.internal.commun.task

import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.executor.RequestExecutor
import com.example.orgcore.internal.commun.model.RequestConstraint
import com.example.orgcore.internal.commun.model.RequestResult
import com.example.orgcore.internal.commun.model.ServerErrorUtil
import com.example.orgcore.internal.commun.processor.PayloadProcessor
import com.example.orgdatabase.public.api.OfflineRequestDataApi
import com.example.orgdatabase.public.model.DBOfflineRequest
import org.json.JSONObject

/**
 * A platform-agnostic [Task] that synchronizes pending offline requests from local database
 * with the remote server using chunked batch processing, payload processors, and circuit breaker protection.
 */
internal class OfflineQueueSyncTask(
    override val id: String = ID,
    private val offlineRequestDataApi: OfflineRequestDataApi,
    private val processorFactory: () -> PayloadProcessor<RequestConstraint>,
    private val requestExecutor: ()->RequestExecutor<Any, RequestConstraint>
) : Task {

    companion object {
        const val ID = "offline_queue_sync"
        private const val BATCH_SIZE = 50
        private const val MAX_TRANSIENT_RETRIES = 10
        private const val CIRCUIT_BREAKER_FAILURE_THRESHOLD = 0.4
        private const val TAG = "SYNC"
        private const val CLASS_NAME = "OfflineQueueSyncTask"
    }

    override val defaultConstraints: TaskConstraints
        get() = TaskConstraints(
            requireNetwork = true,
            trigger = TaskTrigger.OneTime
        )

    override suspend fun execute(): TaskResult {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|execute"
        )
        return try {
            var hasFailure = false

            while (true) {
                val pendingBatch = offlineRequestDataApi.getNextBatchToSync(BATCH_SIZE)
                LoggerHelper.logger.d(
                    TAG,
                    "$CLASS_NAME|execute| number of offline queue: ${pendingBatch.size}"
                )
                if (pendingBatch.isEmpty()) break

                var batchFailures = 0

                pendingBatch.forEach { requestRecord ->
                    // Fresh processor instance per iteration to prevent state pollution across requests
                    val appProcessor = processorFactory()

                    try {
                        val json = JSONObject(requestRecord.payloadJson)
                        val constraint = appProcessor.decode(json)


                        val result = requestExecutor().execute(constraint)

                        LoggerHelper.logger.d(
                            TAG,
                            "$CLASS_NAME|execute|retry result: $result"
                        )

                        when (result) {
                            is RequestResult.Success -> {
                                offlineRequestDataApi.deleteByRequestUuid(requestRecord.requestUuid)
                            }
                            is RequestResult.ServerFailure -> {
                                val isPermanentError = result.errorCode in ServerErrorUtil.PERMANENT_CLIENT_ERRORS

                                if (isPermanentError) {
                                    LoggerHelper.logger.e(TAG, "$CLASS_NAME|execute| Permanent client error (${result.errorCode}). Dropping request.")
                                    offlineRequestDataApi.deleteByRequestUuid(requestRecord.requestUuid)
                                } else {
                                    val shouldRetry = handleTransientFailure(requestRecord)
                                    if (shouldRetry) {
                                        batchFailures++
                                        hasFailure = true
                                    }
                                }
                            }
                            is RequestResult.ConnectionLost -> {
                                batchFailures++
                                hasFailure = true
                                return TaskResult.Retry()
                            }
                            else -> {
                                val shouldRetry = handleTransientFailure(requestRecord)
                                if (shouldRetry) {
                                    batchFailures++
                                    hasFailure = true
                                }
                            }
                        }
                    } catch (e: Exception) {
                        LoggerHelper.logger.e(e,TAG,"$CLASS_NAME|execute|error occurred")
                        batchFailures++
                        hasFailure = true
                    }
                }

                if (pendingBatch.isNotEmpty()) {
                    val failureRate = batchFailures.toDouble() / pendingBatch.size
                    if (failureRate > CIRCUIT_BREAKER_FAILURE_THRESHOLD) {
                        LoggerHelper.logger.w(TAG, "$CLASS_NAME|execute| High failure rate (${failureRate * 100}%). Tripping circuit breaker.")
                        return TaskResult.Retry()
                    }
                }

            }

            if (hasFailure) TaskResult.Retry() else TaskResult.Success
        } catch (e: Exception) {
            LoggerHelper.logger.e(e,TAG,"$CLASS_NAME|execute|error occurred")
            TaskResult.Retry(e)
        }
    }

    private suspend fun handleTransientFailure(requestRecord: DBOfflineRequest): Boolean {
        val newRetryCount = requestRecord.retryCount + 1
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|execute|count: $newRetryCount"
        )
        return if (newRetryCount >= MAX_TRANSIENT_RETRIES) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME|handleTransientFailure| Request exceeded max transient retries ($MAX_TRANSIENT_RETRIES). Dropping.")
            offlineRequestDataApi.deleteByRequestUuid(requestRecord.requestUuid)
            false
        } else {
            val updatedRecord = requestRecord.copy(retryCount = newRetryCount)
            offlineRequestDataApi.addRequest(updatedRecord)
            true
        }
    }
}
