package com.example.orgcore.internal.account.Authentication.provider

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.orgcore.internal.internal.datastore.IntDataStoreProvider
import com.example.orgcore.internal.internal.datastore.StringDataStoreProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthenticationProviderIntegrationTest {

    private class InMemoryDataStore : DataStore<Preferences> {
        private val _data = MutableStateFlow(emptyPreferences())
        override val data: Flow<Preferences> = _data
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            val newData = transform(_data.value)
            _data.value = newData
            return newData
        }
    }

    private lateinit var authenticationProvider: AuthenticationProviderDefault

    @Before
    fun setup() {
        val dataStore = InMemoryDataStore()

        val stringDataStoreProvider = StringDataStoreProvider(dataStore)
        val intDataStoreProvider = IntDataStoreProvider(dataStore)

        authenticationProvider = AuthenticationProviderDefault(stringDataStoreProvider, intDataStoreProvider)
    }

    @Test
    fun `storing and retrieving data through integration`() = runTest {
        val idItem = authenticationProvider.authenticatedUserServerIdDataStoreItemProvider
        val typeItem = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider

        idItem.updateData("test_user_id")
        typeItem.updateData(2)

        assertEquals("test_user_id", idItem.getData("default"))
        assertEquals(2, typeItem.getData(0))
    }

    @Test
    fun `clearing data through integration`() = runTest {
        val idItem = authenticationProvider.authenticatedUserServerIdDataStoreItemProvider
        val typeItem = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider

        idItem.updateData("to_be_cleared")
        typeItem.updateData(1)

        authenticationProvider.clear()

        assertEquals("default", idItem.getData("default"))
        assertEquals(0, typeItem.getData(0))
    }
}
