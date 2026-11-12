package com.example.orgcore.internal.commun.cache

import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgdatabase.public.api.OfflineRequestDataApi
import com.example.orgdatabase.public.model.DBOfflineRequest

internal class OfflineQueueCache(
    private val offlineRequestDataApi: OfflineRequestDataApi
): OfflineCache<DBOfflineRequest> {

    companion object{
        private const val CLASS_NAME = "OfflineQueueCache"
        private const val TAG = "APP"
    }

    override suspend fun cache(data: DBOfflineRequest): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|cache| queue: $data"
        )
        return offlineRequestDataApi.addRequest(data)
    }
}