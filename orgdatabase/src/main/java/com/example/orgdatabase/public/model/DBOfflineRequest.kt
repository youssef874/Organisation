package com.example.orgdatabase.public.model

import androidx.annotation.StringDef

data class DBOfflineRequest(
    val id: Long = 0L,
    val requestUuid: String,
    val requestType: @OfflineRequestType String,
    val priority: Int = 0,
    val payloadJson: String,
    val createdAt: Long,
    val retryCount: Int = 0
)

@StringDef(
    OfflineRequestType.ATTENDANCE
)
@Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
annotation class OfflineRequestType {
    companion object {
        const val ATTENDANCE = "attendance"
    }
}
