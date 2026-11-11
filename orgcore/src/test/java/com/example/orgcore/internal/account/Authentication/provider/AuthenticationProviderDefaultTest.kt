package com.example.orgcore.internal.account.Authentication.provider

import com.example.orgcore.internal.FakeDataStoreProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AuthenticationProviderDefaultTest {

    private lateinit var stringDataStoreProvider: FakeDataStoreProvider<String>
    private lateinit var intDataStoreProvider: FakeDataStoreProvider<Int>
    private lateinit var authenticationProvider: AuthenticationProviderDefault

    @Before
    fun setUp() {
        stringDataStoreProvider = FakeDataStoreProvider()
        intDataStoreProvider = FakeDataStoreProvider()
        authenticationProvider = AuthenticationProviderDefault(stringDataStoreProvider, intDataStoreProvider)
    }

    @Test
    fun `authenticatedUserCredentialDataStoreItemProvider updates string data store`() = runTest {
        val provider = authenticationProvider.authenticatedUserServerIdDataStoreItemProvider
        val testData = "test_id"

        provider.updateData(testData)

        assertEquals(testData, provider.getData("default"))
        assertEquals(testData, stringDataStoreProvider.get(AuthenticationProviderDefault.AUTHENTICATED_USER_SERVER_SERVER_ID))
    }

    @Test
    fun `authenticatedUserCredentialDataStoreItemProvider gets data from string data store`() = runTest {
        val provider = authenticationProvider.authenticatedUserServerIdDataStoreItemProvider
        val testData = "test_id"
        stringDataStoreProvider.put(AuthenticationProviderDefault.AUTHENTICATED_USER_SERVER_SERVER_ID, testData)

        val result = provider.getData("default")

        assertEquals(testData, result)
    }

    @Test
    fun `authenticatedUserCredentialDataStoreItemProvider listens to data changes`() = runTest {
        val provider = authenticationProvider.authenticatedUserServerIdDataStoreItemProvider
        val results = mutableListOf<String?>()
        
        val job = launch {
            provider.listenToDataChanges("default").collect { results.add(it) }
        }
        
        // Ensure the initial value is collected
        kotlinx.coroutines.yield()
        
        provider.updateData("new_id")
        
        // Ensure the updated value is collected
        kotlinx.coroutines.yield()
        
        assertEquals(listOf("default", "new_id"), results)
        job.cancel()
    }

    @Test
    fun `authenticatedUserAccountTypeDataStoreItemProvider updates int data store`() = runTest {
        val provider = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider
        val testData = 1

        provider.updateData(testData)

        assertEquals(testData, provider.getData(0))
        assertEquals(testData, intDataStoreProvider.get(AuthenticationProviderDefault.AUTHENTICATED_USER_ACCOUNT_TYPE))
    }

    @Test
    fun `authenticatedUserAccountTypeDataStoreItemProvider removes data from int data store`() = runTest {
        val provider = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider
        intDataStoreProvider.put(AuthenticationProviderDefault.AUTHENTICATED_USER_ACCOUNT_TYPE, 1)

        provider.remove()

        assertEquals(0, provider.getData(0))
        assertNull(intDataStoreProvider.get(AuthenticationProviderDefault.AUTHENTICATED_USER_ACCOUNT_TYPE))
    }

    @Test
    fun `clear removes all authenticated user data`() = runTest {
        stringDataStoreProvider.put(AuthenticationProviderDefault.AUTHENTICATED_USER_SERVER_SERVER_ID, "id")
        intDataStoreProvider.put(AuthenticationProviderDefault.AUTHENTICATED_USER_ACCOUNT_TYPE, 1)

        authenticationProvider.clear()

        assertEquals("default", authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData("default"))
        assertEquals(0, authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(0))

        assertNull(stringDataStoreProvider.get(AuthenticationProviderDefault.AUTHENTICATED_USER_SERVER_SERVER_ID))
        assertNull(intDataStoreProvider.get(AuthenticationProviderDefault.AUTHENTICATED_USER_ACCOUNT_TYPE))
    }
}
