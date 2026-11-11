package com.example.orgdatabase.internal.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.example.orgdatabase.internal.BaseDao
import com.example.orgdatabase.internal.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AttendanceDao : BaseDao<AttendanceEntity> {

    @Query("DELETE FROM ${AttendanceEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("DELETE FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId")
    suspend fun deleteAttendanceByAccountId(accountId: Long)

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId")
    suspend fun getAttendanceByAccountId(accountId: Long): List<AttendanceEntity>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId")
    fun observeAttendanceByAccountId(accountId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId LIMIT :limit OFFSET :offset")
    suspend fun getAttendanceByAccountIdChunked(accountId: Long, limit: Int, offset: Int): List<AttendanceEntity>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId")
    fun getAttendanceByAccountIdPaged(accountId: Long): PagingSource<Int, AttendanceEntity>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} = :date LIMIT 1")
    suspend fun getAttendanceByAccountAndDate(accountId: Long, date: Long): AttendanceEntity?

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_SERVER_ID} = :serverId LIMIT 1")
    suspend fun getAttendanceByServerId(serverId: String): AttendanceEntity?

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} = :date LIMIT 1")
    fun observeAttendanceByAccountAndDate(accountId: Long, date: Long): Flow<AttendanceEntity?>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} BETWEEN :startDate AND :endDate")
    suspend fun getAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): List<AttendanceEntity>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} BETWEEN :startDate AND :endDate")
    fun observeAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} BETWEEN :startDate AND :endDate LIMIT :limit OFFSET :offset")
    suspend fun getAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, limit: Int, offset: Int): List<AttendanceEntity>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} BETWEEN :startDate AND :endDate LIMIT :limit OFFSET :offset")
    fun observeAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, limit: Int, offset: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM ${AttendanceEntity.TABLE_NAME} WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} BETWEEN :startDate AND :endDate")
    fun getAttendancesByDateRangePaged(accountId: Long, startDate: Long, endDate: Long): PagingSource<Int, AttendanceEntity>

    @Query("UPDATE ${AttendanceEntity.TABLE_NAME} SET ${AttendanceEntity.COLUMN_ATTENDANCE_STATUS} = :status WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId AND ${AttendanceEntity.COLUMN_DATE} = :date")
    suspend fun updateAttendanceStatus(accountId: Long, date: Long, status: String): Int

    @Query("""
        SELECT COUNT(*) FROM ${AttendanceEntity.TABLE_NAME} 
        WHERE ${AttendanceEntity.COLUMN_ACCOUNT_ID} = :accountId 
        AND ${AttendanceEntity.COLUMN_ATTENDANCE_STATUS} = :attendanceStatus 
        AND ${AttendanceEntity.COLUMN_DATE} BETWEEN :startDate AND :endDate
    """)
    suspend fun getAttendanceCountByStatusAndDateRange(
        accountId: Long,
        attendanceStatus: String,
        startDate: Long,
        endDate: Long
    ): Int
}
