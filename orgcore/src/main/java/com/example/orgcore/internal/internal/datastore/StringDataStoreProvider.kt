package com.example.orgcore.internal.internal.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

internal class StringDataStoreProvider(dataStore: DataStore<Preferences>): DataStoreProviderDefault<String>(dataStore) {
    override fun getPreferenceKey(key: String): Preferences.Key<String> {
        return stringPreferencesKey(key)
    }
}