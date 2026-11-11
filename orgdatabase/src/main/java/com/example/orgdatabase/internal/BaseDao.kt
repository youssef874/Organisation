package com.example.orgdatabase.internal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

/**
 * This represents a base structure for any dao in the sdk, so all the dao will inherit from this interface
 * This is generic to give flexibility to the dao that inherit from this interface to specify which entity
 * to be dealing with
 */

@Dao
internal interface BaseDao<T> {

    @Upsert
    suspend fun upsert(data: T): Long

    @Upsert
    suspend fun upsert(data: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<T>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: T): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(data: List<T>): Int

    @Delete
    suspend fun delete(data: T): Int

    @Delete
    suspend fun delete(data: List<T>): Int
}
