package com.example.orgcore.internal

import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakeDataStoreItemProvider<T>: DataStoreItemProvider<T> {

    private var value: T? = null
    private var notify: (suspend (T?) -> Unit)? = null

    override suspend fun updateData(data: T) {
        value = data
        notify?.invoke(data)
    }

    override fun listenToDataChanges(defaultData: T?): Flow<T?> {
        return callbackFlow {
            send(value)
            notify = { send(it) }
            awaitClose { notify = null }
        }
    }

    override suspend fun getData(defaultData: T?): T? {
        return value
    }

    override suspend fun remove(): T? {
        val removedValue = value
        value = null
        notify = null
        return removedValue
    }
}