package com.example.orgdatabase.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = EmailEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = [AccountEntity.COLUMN_ID],
            childColumns = [EmailEntity.COLUMN_ACCOUNT_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [EmailEntity.COLUMN_ACCOUNT_ID])]
)
internal data class EmailEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Long = 0,
    @ColumnInfo(name = COLUMN_EMAIL)
    val email: String,
    @ColumnInfo(name = COLUMN_ACCOUNT_ID)
    val accountId: Long
) {
    companion object {
        const val TABLE_NAME = "email"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ACCOUNT_ID = "account_id"
    }
}
