package com.example.orgcore

import android.content.Context
import androidx.startup.Initializer
import com.example.orgcore.internal.commun.task.TaskRegistryHolder
import com.example.orgcore.internal.commun.task.TaskSchedulerProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This class responsible on initializing the application context during app startup
 * to provide it to the sdk
 */
internal class ContextInitializer: Initializer<Unit> {


    override fun create(context: Context) {
        SDKComponent.SDKContext.initialize(context)

        val scheduler = TaskSchedulerProvider.getOfflineQueueTaskScheduler(context)
        val allTasks = TaskRegistryHolder.workManagerRegistry.getAllTasks()
        if (allTasks.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                allTasks.values.forEach { task ->
                    scheduler.schedule(task, task.defaultConstraints)
                }
            }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}
