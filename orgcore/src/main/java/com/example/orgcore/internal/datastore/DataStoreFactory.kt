package com.youssefmkh.core.internal.commun.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.orgcore.internal.internal.datastore.BooleanDataStoreProvider
import com.example.orgcore.internal.internal.datastore.DataStoreProvider
import com.example.orgcore.internal.internal.datastore.IntDataStoreProvider
import com.example.orgcore.internal.internal.datastore.StringDataStoreProvider

internal object DataStoreFactory {

    private const val FILE_NAME = "data_store"

    @Volatile
    private var dataStore: DataStore<Preferences>? = null

    private fun getDataStore(context: Context): DataStore<Preferences>{
        return dataStore?: synchronized(this){
            val data = PreferenceDataStoreFactory.create {
                val path = FILE_NAME
                context.preferencesDataStoreFile(path)
            }
            dataStore = data
            data
        }
    }

    fun getStringDataStoreProvider(context: Context): DataStoreProvider<String> =
        StringDataStoreProvider(
            getDataStore(context)
        )

    fun getBooleanDataStoreProvider(context: Context): DataStoreProvider<Boolean> =
        BooleanDataStoreProvider(
            getDataStore(context)
        )

    fun getIntegerDataStoreProvider(context: Context): DataStoreProvider<Int> =
        IntDataStoreProvider(getDataStore(context))
}