package com.example.orgcore.internal.account.service

import com.example.orgcore.api.account.event.AccountFetchDataResult
import com.example.orgcore.commun.OrgEventNotifier
import com.example.orgcore.commun.Synchronizer
import com.example.orgcore.commun.model.OrgEvent
import com.example.orgcore.commun.model.SyncResult
import com.example.orgcore.internal.account.Authentication.provider.FakeAuthenticationProvider
import com.example.orgcore.internal.network.NetworkMonitor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AccountServiceImplTest {

    @Mock
    private lateinit var accountSynchronizer: Synchronizer
    @Mock
    private lateinit var networkMonitor: NetworkMonitor
    
    private lateinit var accountEventNotifier: FakeOrgEventNotifier
    private lateinit var authenticationProvider: FakeAuthenticationProvider
    private lateinit var accountService: AccountServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        accountEventNotifier = FakeOrgEventNotifier()
        authenticationProvider = FakeAuthenticationProvider()
        
        runBlocking {
            `when`(accountSynchronizer.isSynchronized()).thenReturn(false)
        }

        accountService = AccountServiceImpl(
            accountSynchronizer,
            networkMonitor,
            accountEventNotifier
        )
    }

    @Test
    fun `requestData returns NetworkError when not connected`() = runTest {
        `when`(networkMonitor.isConnected()).thenReturn(false)

        accountService.requestData()

        val lastEvent = accountEventNotifier.lastEvent as? AccountFetchDataResult
        assertTrue(lastEvent?.result is SyncResult.Error.NetworkError)
    }

    @Test
    fun `requestData delegates to synchronizer when authenticated and connected`() = runTest {
        `when`(networkMonitor.isConnected()).thenReturn(true)
        authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.updateData("user123")
        `when`(accountSynchronizer.synchronize()).thenReturn(SyncResult.Success)

        accountService.requestData()

        val lastEvent = accountEventNotifier.lastEvent as? AccountFetchDataResult
        assertEquals(SyncResult.Success, lastEvent?.result)
    }

    @Test
    fun `requestData returns error when synchronizer fails`() = runTest {
        `when`(networkMonitor.isConnected()).thenReturn(true)
        authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.updateData("user123")
        val expectedError = SyncResult.Error.DatabaseError()
        `when`(accountSynchronizer.synchronize()).thenReturn(expectedError)

        accountService.requestData()

        val lastEvent = accountEventNotifier.lastEvent as? AccountFetchDataResult
        assertEquals(expectedError, lastEvent?.result)
    }

    // Fakes
    private class FakeOrgEventNotifier : OrgEventNotifier {
        var lastEvent: OrgEvent? = null
        override fun notify(event: OrgEvent) {
            lastEvent = event
        }
    }
}
