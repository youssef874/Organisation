package com.example.orgcore.internal.internal.datastore

import kotlinx.coroutines.flow.Flow

/**
 * This represent as data provider for a single item from datastore
 */
internal interface DataStoreItemProvider<T> {

    /**
     * Call this to update the data
     *
     * @param key: The data identifier
     * @param data: the data to be updated to
     */
    suspend fun updateData(data: T)

    /**
     * Call this to listen to the data changes
     *
     * @param key: The data identifier to listen for it changes
     * @param defaultData: if no data existed with the provided key default value will be dispatcher instead
     *
     * @return [Flow] of the data
     */
    fun listenToDataChanges(defaultData: T?): Flow<T?>

    /**
     * Call this to get the existing data
     *
     * @param key: The data identifier to request the data for
     * @param defaultData: if no data existed with the provided key default value will be provided instead
     *
     * @return the existing data or the default value
     */
    suspend fun getData( defaultData: T?): T?

    /**
     * Call this to delete the item
     *
     * @param key:The data identifier to be deleted with
     *
     * @return the deleted item if it exist otherwise it will return null
     */
    suspend fun remove(): T?

}