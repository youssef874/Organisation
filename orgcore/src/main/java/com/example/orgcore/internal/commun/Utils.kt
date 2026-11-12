package com.example.orgcore.internal.commun

import com.example.orgcore.api.model.RequestPriority
import com.example.orgdatabase.public.model.DBOfflineRequest
import com.example.orgdatabase.public.model.OfflineRequestType
import java.util.UUID


internal fun createOfflineCache(
    priority: @RequestPriority Int,
    requestType: @OfflineRequestType String,
    payload: String
) = DBOfflineRequest(
    requestUuid = UUID.randomUUID().toString(),
    priority = priority,
    requestType = requestType,
    payloadJson = payload,
    createdAt = System.currentTimeMillis()
)