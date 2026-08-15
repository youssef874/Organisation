package com.example.orgcore.internal.account

import com.example.orgcore.internal.FakeDataStoreProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountSettingsImplTest {

    private lateinit var booleanDataStoreProvider: FakeDataStoreProvider<Boolean>
    private lateinit var accountSettings: AccountSettingsImpl

    @Before
    fun setUp() {
        booleanDataStoreProvider = FakeDataStoreProvider()
        accountSettings = AccountSettingsImpl(booleanDataStoreProvider)
    }

    @Test
    fun `isUserAccountSynchronized updateData persists value`() = runTest {
        val provider = accountSettings.isUserAccountSynchronized
        
        provider.updateData(true)
        
        assertTrue(booleanDataStoreProvider.get("is_user_account_cached") == true)
        assertEquals(true, provider.getData(false))
    }

    @Test
    fun `isUserAccountSynchronized getData returns default when empty`() = runTest {
        val provider = accountSettings.isUserAccountSynchronized
        provider.remove()
        
        assertEquals(false, provider.getData(false))
        assertEquals(true, provider.getData(true))
    }

    @Test
    fun `isUserAccountSynchronized remove clears value`() = runTest {
        val provider = accountSettings.isUserAccountSynchronized
        provider.updateData(true)
        
        val removedValue = provider.remove()
        
        assertEquals(true, removedValue)
        assertNull(booleanDataStoreProvider.get("is_user_account_cached"))
        assertEquals(false, provider.getData(false))
    }

    @Test
    fun `isUserAccountSynchronized listenToDataChanges emits updates`() = runTest {
        val provider = accountSettings.isUserAccountSynchronized
        val results = mutableListOf<Boolean?>()
        
        val job = launch(UnconfinedTestDispatcher()) {
            provider.listenToDataChanges(false).collect { results.add(it) }
        }
        
        provider.updateData(true)
        provider.updateData(false)
        
        assertEquals(listOf(false, true, false), results)
        job.cancel()
    }

    @Test
    fun `isRelatedUserAccountSynchronized updateData persists value`() = runTest {
        val provider = accountSettings.isRelatedUserAccountSynchronized
        
        provider.updateData(true)
        
        assertTrue(booleanDataStoreProvider.get("is_user_related_account_cached") == true)
        assertEquals(true, provider.getData(false))
    }

    @Test
    fun `isRelatedUserAccountSynchronized remove clears value`() = runTest {
        val provider = accountSettings.isRelatedUserAccountSynchronized
        provider.updateData(true)
        
        val removedValue = provider.remove()
        
        assertEquals(true, removedValue)
        assertNull(booleanDataStoreProvider.get("is_user_related_account_cached"))
    }
}
