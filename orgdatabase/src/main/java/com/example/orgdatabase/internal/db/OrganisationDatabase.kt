package com.example.orgdatabase.internal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.orgdatabase.internal.dao.AccountDao
import com.example.orgdatabase.internal.dao.EmailDao
import com.example.orgdatabase.internal.dao.PhoneNumberDao
import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity

@Database(
    entities = [
        AccountEntity::class,
        EmailEntity::class,
        PhoneNumberEntity::class
    ],
    version = OrganisationDatabase.DATABASE_VERSION,
    exportSchema = false
)
internal abstract class OrganisationDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun emailDao(): EmailDao
    abstract fun phoneNumberDao(): PhoneNumberDao

    companion object {
        const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: OrganisationDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(context, OrganisationDatabase::class.java, "database").build()
            INSTANCE = instance
            instance
        }
    }
}
