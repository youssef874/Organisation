package com.example.orgdatabase.internal.mapper

import com.example.orgdatabase.internal.entity.OfflineRequestEntity
import com.example.orgdatabase.public.model.DBOfflineRequest

internal fun OfflineRequestEntity.toDBOfflineRequest(): DBOfflineRequest {
    return DBOfflineRequest(
        id = id,
        requestUuid = requestUuid,
        requestType = requestType,
        priority = priority,
        payloadJson = payloadJson,
        createdAt = createdAt,
        retryCount = retryCount
    )
}

internal fun DBOfflineRequest.toOfflineRequestEntity(): OfflineRequestEntity {
    return OfflineRequestEntity(
        id = id,
        requestUuid = requestUuid,
        requestType = requestType,
        priority = priority,
        payloadJson = payloadJson,
        createdAt = createdAt,
        retryCount = retryCount
    )
}
