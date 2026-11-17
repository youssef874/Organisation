package com.example.orgcore.internal.commun

import com.example.orgcore.internal.commun.model.RequestPriority
import com.example.orgdatabase.public.model.DBOfflineRequest
import com.example.orgdatabase.public.model.OfflineRequestType
import java.util.UUID


internal fun createOfflineCache(
    requestUuid: String = UUID.randomUUID().toString(),
    priority: @RequestPriority Int,
    requestType: @OfflineRequestType String,
    payload: String
) = DBOfflineRequest(
    requestUuid = requestUuid,
    priority = priority,
    requestType = requestType,
    payloadJson = payload,
    createdAt = System.currentTimeMillis()
)
