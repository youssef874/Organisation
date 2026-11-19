package com.example.orgcore.internal.commun.task

/**
 * Orchestrates the scheduling and cancellation of asynchronous [Task] units.
 */
internal interface TaskScheduler {
    /**
     * Schedules a task for execution with the specified constraints.
     */
    suspend fun schedule(task: Task, constraints: TaskConstraints)

    /**
     * Cancels a scheduled or running task by its unique ID.
     */
    suspend fun cancel(taskId: String)
}
