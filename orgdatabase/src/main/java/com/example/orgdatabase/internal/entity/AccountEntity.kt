package com.example.orgdatabase.internal.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = AccountEntity.TABLE_NAME)
internal data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Long = 0,
    @ColumnInfo(name = COLUMN_NAME)
    val name: String,
    @ColumnInfo(name = COLUMN_PHOTO_URI)
    val photoUri: String? = null,
    @ColumnInfo(name = COLUMN_IS_USER)
    val isUser: Boolean,
    @ColumnInfo(name = COLUMN_ACCOUNT_TYPE)
    val accountType: Int, // 0: Boss, 1: Manager, 2: Employee
    @ColumnInfo(name = COLUMN_SERVER_ID)
    val serverId: String?,
    @ColumnInfo(name = COLUMN_POSITION)
    val position: String? = null,
    @ColumnInfo(name = COLUMN_SUPERVISOR_ID)
    val supervisorId: Long? = null
) {
    companion object {
        const val TABLE_NAME = "account"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PHOTO_URI = "photo_uri"
        const val COLUMN_IS_USER = "is_user"
        const val COLUMN_ACCOUNT_TYPE = "account_type"
        const val COLUMN_SERVER_ID = "server_id"

        const val COLUMN_POSITION = "position"

        const val COLUMN_SUPERVISOR_ID = "supervisor_id"

        const val ACCOUNT_TYPE_BOSS = 0
        const val ACCOUNT_TYPE_MANAGER = 1
        const val ACCOUNT_TYPE_EMPLOYEE = 2
    }
}
