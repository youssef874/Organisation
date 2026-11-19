package com.example.orgcore.internal.commun.task

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.orgcore.internal.commun.LoggerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

internal object TaskRegistryHolder {
    val workManagerRegistry: TaskRegistry = WorkManagerTaskRegistry()
}

internal class WorkManagerTaskRegistry : TaskRegistry {
    private val tasks = mutableMapOf<String, Task>()

    override fun register(task: Task) {
        tasks[task.id] = task
    }

    override fun getTask(taskId: String): Task? = tasks[taskId]

    override fun getAllTasks(): Map<String, Task> = tasks.toMap()
}

internal class WorkManagerTaskScheduler(
    private val context: Context
) : TaskScheduler {

    companion object {
        private const val TAG = "TASK"
        private const val CLASS_NAME = "WorkManagerTaskScheduler"
        internal const val KEY_TASK_ID = "task_id"
    }

    override suspend fun schedule(task: Task, constraints: TaskConstraints) {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|schedule| taskId: ${task.id}, trigger: ${constraints.trigger}, requireNetwork: ${constraints.requireNetwork}")

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (constraints.requireNetwork) NetworkType.CONNECTED else NetworkType.NOT_REQUIRED
            )
            .setRequiresCharging(constraints.requireCharging)
            .build()

        when (val trigger = constraints.trigger) {
            is TaskTrigger.OneTime -> {
                val workRequest = OneTimeWorkRequestBuilder<TaskWorker>()
                    .setConstraints(workConstraints)
                    .setInputData(workDataOf(KEY_TASK_ID to task.id))
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    task.id,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }
            is TaskTrigger.Periodic -> {
                val workRequest = PeriodicWorkRequestBuilder<TaskWorker>(
                    trigger.intervalMillis,
                    TimeUnit.MILLISECONDS
                )
                    .setConstraints(workConstraints)
                    .setInputData(workDataOf(KEY_TASK_ID to task.id))
                    .build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    task.id,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
            }
        }
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|schedule| task scheduled successfully: ${task.id}")
    }

    override suspend fun cancel(taskId: String) {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|cancel| taskId: $taskId")
        WorkManager.getInstance(context).cancelUniqueWork(taskId)
    }
}

class TaskWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "TASK"
        private const val CLASS_NAME = "TaskWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val taskId = inputData.getString(WorkManagerTaskScheduler.KEY_TASK_ID)
        if (taskId == null) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME|doWork| task_id not found in input data")
            return@withContext Result.failure()
        }

        LoggerHelper.logger.i(TAG, "$CLASS_NAME|doWork| starting execution for taskId: $taskId")
        val task = TaskRegistryHolder.workManagerRegistry.getTask(taskId)
        if (task == null) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME|doWork| no task found in registry for taskId: $taskId")
            return@withContext Result.failure()
        }

        return@withContext when (val executionResult = task.execute()) {
            is TaskResult.Success -> {
                LoggerHelper.logger.i(TAG, "$CLASS_NAME|doWork| task succeeded: $taskId")
                Result.success()
            }
            is TaskResult.Retry -> {
                LoggerHelper.logger.w(TAG, "$CLASS_NAME|doWork| task requested retry: $taskId, reason: ${executionResult.throwable?.message}")
                Result.retry()
            }
            is TaskResult.Failure -> {
                LoggerHelper.logger.e(TAG, "$CLASS_NAME|doWork| task failed: $taskId, reason: ${executionResult.throwable?.message}")
                Result.failure()
            }
        }
    }
}
