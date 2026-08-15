package com.example.orgdatabase.internal.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.orgdatabase.internal.BaseDao
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PhoneNumberDao : BaseDao<PhoneNumberEntity> {

    @Query("SELECT * FROM ${PhoneNumberEntity.TABLE_NAME} WHERE ${PhoneNumberEntity.COLUMN_ACCOUNT_ID} = :accountId")
    fun getPhoneNumbersForAccount(accountId: Long): Flow<List<PhoneNumberEntity>>

    @Query("DELETE FROM ${PhoneNumberEntity.TABLE_NAME} WHERE ${PhoneNumberEntity.COLUMN_ACCOUNT_ID} = :accountId")
    suspend fun deletePhoneNumbersByAccountId(accountId: Long): Int
}
