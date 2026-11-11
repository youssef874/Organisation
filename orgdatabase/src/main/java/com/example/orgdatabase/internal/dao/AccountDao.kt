package com.example.orgdatabase.internal.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.orgdatabase.internal.BaseDao
import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.AccountWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AccountDao : BaseDao<AccountEntity> {

    @Query("DELETE FROM ${AccountEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME}")
    suspend fun getAllAccounts(): List<AccountEntity>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME}")
    fun observeAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_ID} = :id")
    suspend fun getAccountById(id: Long): AccountEntity?

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_SERVER_ID} = :serverId LIMIT 1")
    suspend fun getAccountByServerId(serverId: String): AccountEntity?

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_SERVER_ID} = :serverId LIMIT 1")
    suspend fun getAccountWithDetailsByServerIdSuspend(serverId: String): AccountWithDetails?

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME}")
    fun getAllAccountsWithDetails(): Flow<List<AccountWithDetails>>

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME}")
    suspend fun getAllAccountsWithDetailsSuspend(): List<AccountWithDetails>

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_ID} = :id")
    fun getAccountWithDetailsById(id: Long): Flow<AccountWithDetails?>

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_ID} = :id")
    suspend fun getAccountWithDetailsByIdSuspend(id: Long): AccountWithDetails?

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME}")
    fun getAccountsWithDetailsPaged(): PagingSource<Int, AccountWithDetails>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} LIMIT :limit OFFSET :offset")
    suspend fun getAccountsByChunk(limit: Int, offset: Int): List<AccountEntity>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} LIMIT :limit OFFSET :offset")
    fun getAccountsByChunkFlow(limit: Int, offset: Int): Flow<List<AccountEntity>>


    @Query("SELECT COUNT(*) FROM ${AccountEntity.TABLE_NAME}")
    suspend fun getAccountCount(): Int

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} where ${AccountEntity.COLUMN_IS_USER} = 1 LIMIT 1")
    suspend fun getUserAccount(): AccountEntity?

    @Transaction
    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} where ${AccountEntity.COLUMN_IS_USER} = 1 LIMIT 1")
    suspend fun getUserAccountWithDetailsSuspend(): AccountWithDetails?

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} where ${AccountEntity.COLUMN_IS_USER} = 1 LIMIT 1")
    fun getUserAccountFlow(): Flow<AccountEntity?>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_SUPERVISOR_ID} = :assignedId")
    suspend fun getAllAssignedAccounts(assignedId: Long): List<AccountEntity>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_SUPERVISOR_ID} = :assignedId")
    fun getAllAssignedAccountsFlow(assignedId: Long): Flow<List<AccountEntity>>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_SUPERVISOR_ID} = :assignedId")
    fun getAllAssignedAccountsPaged(assignedId: Long): PagingSource<Int, AccountEntity>

    @Query("SELECT * FROM ${AccountEntity.TABLE_NAME} WHERE ${AccountEntity.COLUMN_SUPERVISOR_ID} = :assignedId LIMIT :limit OFFSET :offset")
    suspend fun getAllAssignedAccountsChunked(assignedId: Long, limit: Int, offset: Int): List<AccountEntity>
}
