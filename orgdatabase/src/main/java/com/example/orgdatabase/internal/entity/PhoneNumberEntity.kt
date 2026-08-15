package com.example.orgdatabase.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = PhoneNumberEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = [AccountEntity.COLUMN_ID],
            childColumns = [PhoneNumberEntity.COLUMN_ACCOUNT_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [PhoneNumberEntity.COLUMN_ACCOUNT_ID])]
)
internal data class PhoneNumberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Long = 0,
    @ColumnInfo(name = COLUMN_NUMBER)
    val number: String,
    @ColumnInfo(name = COLUMN_ACCOUNT_ID)
    val accountId: Long
) {
    companion object {
        const val TABLE_NAME = "phone_number"
        const val COLUMN_ID = "id"
        const val COLUMN_NUMBER = "number"
        const val COLUMN_ACCOUNT_ID = "account_id"
    }
}
