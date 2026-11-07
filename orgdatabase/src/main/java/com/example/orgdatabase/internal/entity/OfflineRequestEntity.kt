package com.example.orgdatabase.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = OfflineRequestEntity.TABLE_NAME,
    indices = [
        Index(value = [OfflineRequestEntity.COLUMN_REQUEST_UUID], unique = true),
        Index(value = [OfflineRequestEntity.COLUMN_PRIORITY, OfflineRequestEntity.COLUMN_CREATED_AT]),
        Index(value = [OfflineRequestEntity.COLUMN_REQUEST_TYPE])
    ]
)
internal data class OfflineRequestEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Long = 0L,

    @ColumnInfo(name = COLUMN_REQUEST_UUID)
    val requestUuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = COLUMN_REQUEST_TYPE)
    val requestType: String,

    @ColumnInfo(name = COLUMN_PRIORITY)
    val priority: Int = PRIORITY_NORMAL,

    @ColumnInfo(name = COLUMN_PAYLOAD_JSON)
    val payloadJson: String,

    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = COLUMN_RETRY_COUNT)
    val retryCount: Int = 0
) {
    companion object {
        const val TABLE_NAME = "offline_requests"
        const val COLUMN_ID = "id"
        const val COLUMN_REQUEST_UUID = "request_uuid"
        const val COLUMN_REQUEST_TYPE = "request_type"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_PAYLOAD_JSON = "payload_json"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_RETRY_COUNT = "retry_count"

        // Request types
        const val REQUEST_TYPE_ATTENDANCE = "attendance"

        // Priority levels
        const val PRIORITY_LOW = -5
        const val PRIORITY_NORMAL = 0
        const val PRIORITY_HIGH = 10
    }
}
