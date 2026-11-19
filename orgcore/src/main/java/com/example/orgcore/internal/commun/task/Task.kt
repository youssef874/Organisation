package com.example.orgcore.internal.commun.task

/**
 * Represents a discrete unit of background work that can be executed asynchronously.
 */
internal interface Task {
    /** Unique identifier for the task. */
    val id: String

    /** Default execution constraints for the task. */
    val defaultConstraints: TaskConstraints

    /** Executes the task work and returns the outcome result. */
    suspend fun execute(): TaskResult
}

/**
 * Represents the exhaustive outcome of a [Task] execution.
 */
internal sealed interface TaskResult {
    /** Task completed successfully. */
    object Success : TaskResult

    /** Task encountered a transient failure and should be retried. */
    data class Retry(val throwable: Throwable? = null) : TaskResult

    /** Task encountered a permanent failure and should not be retried. */
    data class Failure(val throwable: Throwable? = null) : TaskResult
}

/**
 * Defines the trigger frequency and scheduling mode for a task.
 */
internal sealed interface TaskTrigger {
    /** Task executes once. */
    object OneTime : TaskTrigger

    /** Task executes periodically at specified interval intervals. */
    data class Periodic(val intervalMillis: Long) : TaskTrigger
}

/**
 * Constraints required for task execution.
 */
internal data class TaskConstraints(
    val requireNetwork: Boolean = false,
    val requireCharging: Boolean = false,
    val trigger: TaskTrigger = TaskTrigger.OneTime,
    val priority: Int = 0
)
