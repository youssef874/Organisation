package com.example.orgcore.internal.commun.task

/**
 * Registry repository for managing and looking up registered [Task] instances.
 */
internal interface TaskRegistry {
    /** Registers a task in the registry. */
    fun register(task: Task)

    /** Retrieves a task by its unique ID, or null if not found. */
    fun getTask(taskId: String): Task?

    /** Returns an immutable map of all registered tasks. */
    fun getAllTasks(): Map<String, Task>
}
