package com.example.orgcore.api.account.authentication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.orgcore.SDKComponent
import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.commun.model.Result
import com.example.orgcore.internal.ServicesProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthenticationE2ETest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication().applicationContext
        SDKComponent.SDKContext.initialize(context)
        
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // Ensure clean state before each test
        runTest {
            ServicesProvider.getAuthenticationProvider(context).clear()
        }
        
        // Default to connected for most tests
        setNetworkConnected(true)
    }

    @Test
    fun `full authentication success flow for admin`() = runTest {
        assertFalse(AuthenticationApi.isUserAuthenticated())
        
        val result = AuthenticationApi.requestAuthentication("admin", "psw")
        
        assertTrue("Expected Success but got $result", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals("Admin", data.name)
        assertEquals(AccountUserType.BOSS, data.accountUserType)
        
        assertTrue(AuthenticationApi.isUserAuthenticated())
        assertEquals(AccountUserType.BOSS, AuthenticationApi.getAuthenticatedAccountType())
    }

    @Test
    fun `authentication failure flow - invalid credentials`() = runTest {
        val result = AuthenticationApi.requestAuthentication("wrong_user", "wrong_psw")
        
        assertTrue("Expected Error but got $result", result is Result.Error)
        assertEquals(AuthenticationErrorType.AUTHENTICATION_REQUEST_NOT_VALIDATED, (result as Result.Error).error.errorCode)
        
        assertFalse(AuthenticationApi.isUserAuthenticated())
    }

    @Test
    fun `authentication failure flow - no network`() = runTest {
        setNetworkConnected(false)
        
        val result = AuthenticationApi.requestAuthentication("admin", "psw")
        
        assertTrue("Expected Error but got $result", result is Result.Error)
        assertEquals(AuthenticationErrorType.NO_NETWORK, (result as Result.Error).error.errorCode)
        
        assertFalse(AuthenticationApi.isUserAuthenticated())
    }

    @Test
    fun `authentication failure flow - timeout`() = runTest {
        val result = AuthenticationApi.requestAuthentication("timeout", "any")
        
        assertTrue("Expected Error but got $result", result is Result.Error)
        assertEquals(AuthenticationErrorType.TIMEOUT, (result as Result.Error).error.errorCode)
        
        assertFalse(AuthenticationApi.isUserAuthenticated())
    }

    @Test
    fun `cannot login if already authenticated`() = runTest {
        // First login
        AuthenticationApi.requestAuthentication("worker", "psw")
        assertTrue(AuthenticationApi.isUserAuthenticated())
        
        // Second login attempt
        val result = AuthenticationApi.requestAuthentication("admin", "psw")
        
        assertTrue("Expected ALREADY_AUTHENTICATED error but got $result", result is Result.Error)
        assertEquals(AuthenticationErrorType.ALREADY_AUTHENTICATED, (result as Result.Error).error.errorCode)
    }

    private fun setNetworkConnected(connected: Boolean) {
        val shadowConnectivityManager = shadowOf(connectivityManager)
        if (connected) {
            shadowConnectivityManager.setDefaultNetworkActive(true)
            val network = connectivityManager.activeNetwork
            val caps = org.mockito.Mockito.mock(NetworkCapabilities::class.java).apply {
                org.mockito.Mockito.`when`(hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
                org.mockito.Mockito.`when`(hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
            }
            shadowConnectivityManager.setNetworkCapabilities(network, caps)
        } else {
            shadowConnectivityManager.setDefaultNetworkActive(false)
            val network = connectivityManager.activeNetwork
            if (network != null) {
                shadowConnectivityManager.setNetworkCapabilities(network, null)
            }
        }
    }
}
