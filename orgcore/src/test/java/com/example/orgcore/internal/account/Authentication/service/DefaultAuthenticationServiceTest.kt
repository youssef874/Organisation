package com.example.orgcore.internal.account.Authentication.service

import android.content.Context
import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.api.network.data.NetworkConnection
import com.example.orgcore.commun.model.Result
import com.example.orgcore.internal.account.Authentication.provider.FakeAuthenticationProvider
import com.example.orgcore.internal.account.Authentication.request.FakeAuthenticationRequest
import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider
import com.example.orgcore.internal.network.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class DefaultAuthenticationServiceTest {

    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var authenticationProvider: FakeAuthenticationProvider
    private lateinit var service: DefaultAuthenticationService
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication().applicationContext
        networkMonitor = FakeNetworkMonitor()
        authenticationProvider = FakeAuthenticationProvider()
        service = DefaultAuthenticationService(
            requestFactory = { userName, psw ->
                FakeAuthenticationRequest(networkMonitor, userName, psw)
            },
            authenticationProvider = authenticationProvider,
        )
    }

    @Test
    fun `requestAuthentication returns ALREADY_AUTHENTICATED error when already logged in`() = runTest {
        authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.updateData("existing_id")
        
        val result = service.requestAuthentication("admin", "any")
        
        assertTrue(result is Result.Error)
        assertEquals(AuthenticationErrorType.ALREADY_AUTHENTICATED, (result as Result.Error).error.errorCode)
    }

    @Test
    fun `requestAuthentication returns NO_NETWORK error when disconnected`() = runTest {
        networkMonitor.isConnectedF = false
        
        val result = service.requestAuthentication("admin", "any")
        
        assertTrue(result is Result.Error)
        assertEquals(AuthenticationErrorType.NO_NETWORK, (result as Result.Error).error.errorCode)
    }

    @Test
    fun `requestAuthentication returns success and updates provider when admin logs in`() = runTest {
        networkMonitor.isConnectedF = true
        
        val result = service.requestAuthentication("admin", "any")
        
        assertTrue(result is Result.Success)
        val account = (result as Result.Success).data
        assertEquals(AccountUserType.BOSS, account.type)
        
        // Verify provider updated
        assertEquals("1", authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData(""))
        assertEquals(AccountUserType.BOSS, authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(-1))
    }

    @Test
    fun `requestAuthentication returns success and updates provider when manager logs in`() = runTest {
        networkMonitor.isConnectedF = true
        
        val result = service.requestAuthentication("manager", "any")
        
        assertTrue(result is Result.Success)
        val account = (result as Result.Success).data
        assertEquals(AccountUserType.MANAGER, account.type)
        
        assertEquals("2", authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData(""))
        assertEquals(AccountUserType.MANAGER, authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(-1))
    }

    @Test
    fun `requestAuthentication returns success and updates provider when worker logs in`() = runTest {
        networkMonitor.isConnectedF = true
        
        val result = service.requestAuthentication("worker", "any")
        
        assertTrue(result is Result.Success)
        val account = (result as Result.Success).data
        assertEquals(AccountUserType.WORKER, account.type)
        
        assertEquals("3", authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData(""))
        assertEquals(AccountUserType.WORKER, authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(-1))
    }

    @Test
    fun `requestAuthentication returns error when invalid user logs in`() = runTest {
        networkMonitor.isConnectedF = true
        
        val result = service.requestAuthentication("unknown", "any")
        
        assertTrue(result is Result.Error)
        assertEquals(AuthenticationErrorType.AUTHENTICATION_REQUEST_NOT_VALIDATED, (result as Result.Error).error.errorCode)
    }

    // Fakes
    private class FakeNetworkMonitor : NetworkMonitor {
        var isConnectedF = true
        override fun isConnected(): Boolean = isConnectedF
        override fun getNetworkStatus(): NetworkConnection = NetworkConnection()
        override fun listenToNetworkChanges(): Flow<NetworkConnection> = MutableStateFlow(NetworkConnection())
    }



    private class FakeDataStoreItemProvider<T : Any> : DataStoreItemProvider<T> {
        private val state = MutableStateFlow<T?>(null)
        override suspend fun updateData(data: T) { state.value = data }
        override fun listenToDataChanges(defaultData: T): Flow<T> = state.map { it ?: defaultData }
        override suspend fun getData(defaultData: T): T = state.value ?: defaultData
        override suspend fun remove(): T {
            val old = state.value
            state.value = null
            return old!!
        }
    }
}
