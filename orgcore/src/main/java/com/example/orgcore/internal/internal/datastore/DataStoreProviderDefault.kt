package com.example.orgcore.internal.internal.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.orgcore.commun.LoggerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal abstract class DataStoreProviderDefault<T>(
    private var dataStore: DataStore<Preferences>
): DataStoreProvider<T> {

    companion object{
        private const val CLASS_NAME = "DataStoreProviderDefault"
        private const val TAG = "DATA_STORE"
    }

    abstract fun getPreferenceKey(key: String): Preferences.Key<T>

    override suspend fun put(key: String, data: T) {
        withContext(Dispatchers.IO){
            LoggerHelper.logger.d(
                tag = TAG,
                message = "$CLASS_NAME|put|key: $key, data: $data"
            )
            dataStore.edit {
                it.set(getPreferenceKey(key), value = data)
            }
        }
    }

    override fun observe(key: String, defaultValue: T): Flow<T> {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|observe|key: $key"
        )
        return dataStore.data.map {
            val prefKey = getPreferenceKey(key)
            it.get(prefKey)?:defaultValue
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun remove(key: String): T? {
        return withContext(Dispatchers.IO){
            var data: T? = null
            LoggerHelper.logger.d(
                TAG,
                "$CLASS_NAME|remove|key: $key"
            )
            dataStore.edit {
                val prefKey = getPreferenceKey(key)
                data = it.remove(prefKey)
            }
            data
        }
    }

    override suspend fun contains(key: String): Boolean {
        return withContext(Dispatchers.IO){
            val prefKey = getPreferenceKey(key)
            return@withContext dataStore.data.map {
                if (it.asMap().isEmpty()){
                    false
                }else{
                    it.contains(prefKey)
                }
            }.first()
        }
    }

    override fun getAll(): Flow<Map<String, Any>> {
        return dataStore.data.map {
            it.asMap().entries.associate { entry ->
                entry.key.toString() to entry.value
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun clear() {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|clear"
        )
        withContext(Dispatchers.IO){
            dataStore.edit {
                it.clear()
            }
        }
    }
}
