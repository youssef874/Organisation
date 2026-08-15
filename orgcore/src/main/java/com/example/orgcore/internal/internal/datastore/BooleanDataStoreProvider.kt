package com.example.orgcore.internal.internal.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

internal class BooleanDataStoreProvider(dataStore: DataStore<Preferences>): DataStoreProviderDefault<Boolean>(dataStore) {
    override fun getPreferenceKey(key: String): Preferences.Key<Boolean> {
        return booleanPreferencesKey(key)
    }
}