package com.example.orgdatabase.internal.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.orgdatabase.internal.BaseDao
import com.example.orgdatabase.internal.entity.OfflineRequestEntity

@Dao
internal interface OfflineRequestDao : BaseDao<OfflineRequestEntity> {

    @Query("DELETE FROM ${OfflineRequestEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("""
        SELECT * FROM ${OfflineRequestEntity.TABLE_NAME} 
        ORDER BY ${OfflineRequestEntity.COLUMN_PRIORITY} DESC, ${OfflineRequestEntity.COLUMN_CREATED_AT} ASC 
        LIMIT :limit
    """)
    suspend fun getNextBatchToSync(limit: Int): List<OfflineRequestEntity>

    @Query("""
        SELECT * FROM ${OfflineRequestEntity.TABLE_NAME} 
        WHERE ${OfflineRequestEntity.COLUMN_REQUEST_TYPE} = :requestType
        ORDER BY ${OfflineRequestEntity.COLUMN_PRIORITY} DESC, ${OfflineRequestEntity.COLUMN_CREATED_AT} ASC 
        LIMIT :limit
    """)
    suspend fun getNextBatchToSyncByType(requestType: String, limit: Int): List<OfflineRequestEntity>

    @Query("SELECT * FROM ${OfflineRequestEntity.TABLE_NAME} WHERE ${OfflineRequestEntity.COLUMN_REQUEST_UUID} = :requestUuid LIMIT 1")
    suspend fun getByRequestUuid(requestUuid: String): OfflineRequestEntity?

    @Query("DELETE FROM ${OfflineRequestEntity.TABLE_NAME} WHERE ${OfflineRequestEntity.COLUMN_REQUEST_UUID} = :requestUuid")
    suspend fun deleteByRequestUuid(requestUuid: String): Int

    @Query("SELECT COUNT(*) FROM ${OfflineRequestEntity.TABLE_NAME}")
    suspend fun getCount(): Int
}
