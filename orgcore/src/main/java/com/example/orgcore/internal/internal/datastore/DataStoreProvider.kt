package com.example.orgcore.internal.internal.datastore

import kotlinx.coroutines.flow.Flow

internal interface DataStoreProvider<T> {

    /**
     * Call this methode to store into datastore
     * @param key: the value or or identification in the datastore
     * @param data: The value to be stored as the new value for the provided key
     */
    suspend fun put(key: String,data: T)

    /**
     * Call this methode to get the value of desired preference that has been identified by the provided key
     * @param key: the value or or identification in the datastore
     * @param defaultValue: the return value if there no value for the requested key
     * @return [Flow] of [T] contain either the existed value if there are any otherwise it will return the provided default value
     */
    fun observe(key: String,defaultValue: T?): Flow<T?>

    /**
     * Remove the data from datastore  with the provided key
     * @param key: the data identifier to be removed
     */
    suspend fun remove(key: String): T?


    /**
     * To check if the provided key exist in datastore or no
     * @param key: the key to find
     * @return true if the key exist otherwise it will return false
     */
    suspend fun contains(key: String): Boolean

    /**
     * Call this method to get all existed value wither key in the datastore
     * @return [Flow] of [Map] of data key and their value
     */
    fun getAll(): Flow<Map<String, Any>>

    /**
     * Delete all data in the datastore
     */
    suspend fun clear()
}