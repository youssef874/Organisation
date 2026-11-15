package com.example.orgcore.internal.commun

import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.model.DBOfflineRequest

internal class FakeOfflineQueueCache : OfflineCache<DBOfflineRequest> {
    var cachedRequest: DBOfflineRequest? = null
    var success: Boolean = true
    override suspend fun cache(data: DBOfflineRequest): Boolean {
        cachedRequest = data
        return success
    }
}
