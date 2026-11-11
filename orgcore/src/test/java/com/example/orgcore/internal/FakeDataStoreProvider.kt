package com.example.orgcore.internal

import com.example.orgcore.internal.internal.datastore.DataStoreProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeDataStoreProvider<T> : DataStoreProvider<T> {
    private val dataFlow = MutableStateFlow<Map<String, T>>(emptyMap())

    override suspend fun put(key: String, data: T) {
        dataFlow.value += (key to data)
    }

    override fun observe(key: String, defaultValue: T): Flow<T> {
        return dataFlow.map { it[key] ?: defaultValue }
    }

    override suspend fun remove(key: String): T? {
        val removedValue = dataFlow.value[key]
        dataFlow.value -= key
        return removedValue
    }

    override suspend fun contains(key: String): Boolean {
        return dataFlow.value.containsKey(key)
    }

    override fun getAll(): Flow<Map<String, Any>> {
        return dataFlow.map { it.mapValues { entry -> entry.value as Any } }
    }

    override suspend fun clear() {
        dataFlow.value = emptyMap()
    }

    fun get(key: String): T? = dataFlow.value[key]
}
