package com.example.orgdatabase.internal.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.orgdatabase.internal.BaseDao
import com.example.orgdatabase.internal.entity.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface EmailDao : BaseDao<EmailEntity> {


    @Query("SELECT * FROM ${EmailEntity.TABLE_NAME} WHERE ${EmailEntity.COLUMN_ACCOUNT_ID} = :accountId")
    fun getEmailsForAccount(accountId: Long): Flow<List<EmailEntity>>

    @Query("DELETE FROM ${EmailEntity.TABLE_NAME} WHERE ${EmailEntity.COLUMN_ACCOUNT_ID} = :accountId")
    suspend fun deleteEmailsByAccountId(accountId: Long): Int
}
