package com.example.orgcore.internal.internal.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

internal class IntDataStoreProvider(dataStore: DataStore<Preferences>): DataStoreProviderDefault<Int>(dataStore) {
    override fun getPreferenceKey(key: String): Preferences.Key<Int> {
        return intPreferencesKey(key)
    }
}
