package com.example.orgcore.api.account

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.orgcore.SDKComponent
import com.example.orgcore.api.account.authentication.AuthenticationApi
import com.example.orgcore.api.account.event.AccountEventTypes
import com.example.orgcore.api.account.event.AccountFetchDataResult
import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.attendance.AttendanceApi
import com.example.orgcore.commun.FakeData
import com.example.orgcore.commun.model.SyncResult
import com.example.orgcore.internal.ServicesProvider
import com.example.orgdatabase.public.api.AccountDataApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
class AccountSyncE2ETest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var accountDataApi: AccountDataApi

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication().applicationContext
        SDKComponent.SDKContext.initialize(context)
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        accountDataApi = AccountDataApi(context)
        
        // Ensure clean state
        runTest {
            ServicesProvider.getAuthenticationProvider(context).clear()
            ServicesProvider.getAccountSettings(context).isUserAccountSynchronized.updateData(false)
            ServicesProvider.getAccountSettings(context).isRelatedUserAccountSynchronized.updateData(false)
            ServicesProvider.getAccountSettings(context).isUserAttendanceRapportSynchronized.updateData(false)
        }
        
        setNetworkConnected(true)
    }

    @Test
    fun `full synchronization flow success`() = runTest {
        // 1. Authenticate user
        val authResult = AuthenticationApi.requestAuthentication("worker", "psw")
        assertTrue("Authentication should succeed", authResult is com.example.orgcore.commun.model.Result.Success)

        // 2. Trigger synchronization
        AccountApi.fetchData()

        // 3. Observe and verify sync result event
        val event = AccountApi.observeAccountEvents(AccountEventTypes.ACCOUNT_FETCH_RESULT)
            .filterIsInstance<AccountFetchDataResult>()
            .first()
        
        assertEquals(SyncResult.Success, event.result)

        // 4. Verify Database Persistence
        val allAccounts = accountDataApi.getAccountsByChunk(0, 10)
        assertTrue("Database should contain cached accounts", allAccounts.isNotEmpty())
        
        val cachedUser = allAccounts.find { it.serverId == "3" }
        assertNotNull("Owner account should be in DB", cachedUser)
        assertTrue("Owner account should be marked as user", cachedUser!!.isUser)

        // 5. Verify Attendance Persistence via AttendanceApi matches expected fake data
        val expectedAttendances = FakeData.getFakeData(AccountUserType.WORKER).attendance
        val cachedAttendances = AttendanceApi.getAttendanceRapportByAccountId(cachedUser.id)
        
        assertEquals("Cached attendances count should match expected", expectedAttendances.size, cachedAttendances.size)
        cachedAttendances.forEachIndexed { index, orgAttendance ->
            assertEquals(expectedAttendances[index].checkIn, orgAttendance.checkIn)
            assertEquals(expectedAttendances[index].checkOut, orgAttendance.checkOut)
            assertEquals(expectedAttendances[index].date, orgAttendance.date)
            assertEquals(expectedAttendances[index].attendanceStatus, orgAttendance.attendanceStatus)
        }

        // 6. Verify Settings flags
        val settings = ServicesProvider.getAccountSettings(context)
        assertTrue(settings.isUserAccountSynchronized.getData(false))
        assertTrue(settings.isRelatedUserAccountSynchronized.getData(false))
        assertTrue(settings.isUserAttendanceRapportSynchronized.getData(false))
    }

    @Test
    fun `sync ignored when already synchronized`() = runTest {
        // 1. Setup as already synchronized
        val settings = ServicesProvider.getAccountSettings(context)
        settings.isUserAccountSynchronized.updateData(true)
        settings.isRelatedUserAccountSynchronized.updateData(true)
        settings.isUserAttendanceRapportSynchronized.updateData(true)

        // 2. Trigger sync
        AccountApi.fetchData()
        
        // 3. Verify success immediately (from cache/guard check)
        val event = AccountApi.observeAccountEvents(AccountEventTypes.ACCOUNT_FETCH_RESULT)
            .filterIsInstance<AccountFetchDataResult>()
            .first()
            
        assertEquals(SyncResult.Success, event.result)
        // Note: Real network check would show 0 calls, but here we verify flow doesn't break
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
