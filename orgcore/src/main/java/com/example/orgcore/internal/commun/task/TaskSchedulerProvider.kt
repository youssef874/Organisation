package com.example.orgcore.internal.commun.task

import android.content.Context
import com.example.orgcore.SDKComponent
import com.example.orgcore.internal.commun.processor.PayloadProcessorProvider
import com.example.orgcore.internal.network.ConnectivityManagerNetworkMonitor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.api.OfflineRequestDataApi

internal object TaskSchedulerProvider {

    fun getOfflineQueueTaskScheduler(context: Context): TaskScheduler {
        val offlineRequestDataApi = OfflineRequestDataApi(context)
        val accountDataApi = AccountDataApi(context)
        val networkMonitor = ConnectivityManagerNetworkMonitor(SDKComponent.SDKContext.getContext())

        val offlineQueueSyncTask = OfflineQueueSyncTask(
            offlineRequestDataApi = offlineRequestDataApi,
            networkMonitor = networkMonitor,
            processorFactory = { PayloadProcessorProvider.getAppPayloadProcessor(accountDataApi) }
        )

        TaskRegistryHolder.workManagerRegistry.register(offlineQueueSyncTask)

        return WorkManagerTaskScheduler(
            context = context
        )
    }
}
