package com.example.orgcore.internal.account

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.commun.anyObject
import com.example.orgcore.commun.capture
import com.example.orgcore.internal.FakeCache
import com.example.orgcore.internal.account.Authentication.mapper.toDBAccount
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.Authentication.provider.FakeAuthenticationProvider
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.account.request.FakeAccountOwnerRequest
import com.example.orgcore.internal.account.request.FakeRequestRelatedUserAccount
import com.example.orgcore.internal.account.sync.RelatedAccountsSynchronizer
import com.example.orgcore.internal.account.sync.UserAccountSynchronizer
import com.example.orgcore.internal.attendance.cache.UserAttendanceRapportCache
import com.example.orgcore.internal.attendance.request.FakeUserAttendanceRapportRequest
import com.example.orgcore.internal.attendance.sync.UserAttendanceRapportSynchronizer
import com.example.orgcore.internal.commun.FakeData
import com.example.orgcore.internal.commun.ServerRequest
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAttendance
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SynchronizerImplementationsTest {

    private val authenticationProvider: AuthenticationProvider = FakeAuthenticationProvider()

    private lateinit var userRequest: ServerRequest<Account>

    private lateinit var userCache: OfflineCache<Account>

    private lateinit var relatedRequest: ServerRequest<List<Account>>

    private lateinit var relatedCache: OfflineCache<List<Account>>


    private var accountSettings: AccountSettings = FakeAccountSetting()

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    private lateinit var attendanceRequest: FakeUserAttendanceRapportRequest

    private lateinit var attendanceCache: UserAttendanceRapportCache

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userRequest = FakeAccountOwnerRequest(
            authenticationProvider
        )
        relatedRequest = FakeRequestRelatedUserAccount(authenticationProvider)
        userCache = FakeCache()
        relatedCache = FakeCache()
        attendanceRequest = FakeUserAttendanceRapportRequest(authenticationProvider)
        attendanceCache = UserAttendanceRapportCache(accountDataApi)
    }

    @Test
    fun `UserAccountSynchronizer delegates to cache and updates settings`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        val account = FakeData.getFakeData(AccountUserType.WORKER).account


        val sync = UserAccountSynchronizer(userRequest, userCache, accountSettings)
        val result = sync.synchronize()

        assertEquals(SyncResult.Success, result)
        val cache = userCache as FakeCache<Account>
        assertEquals(1, cache.cachedItems.size)
        assertEquals(account, cache.cachedItems.first())
        assert(accountSettings.isUserAccountSynchronized.getData(false) == true)
        assertTrue(sync.isSynchronized())
    }

    @Test
    fun `RelatedAccountsSynchronizer chunks large lists and updates settings`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )

        val largeAccountList = List(205) { index ->
            Account(
                id = index.toString(),
                name = "User $index",
                email = "user$index@gmail.com",
                phoneNumber = "12345678$index",
                image = null,
                type = AccountUserType.WORKER
            )
        }
        
        (relatedRequest as FakeRequestRelatedUserAccount).accountsOverride = largeAccountList
        
        val sync = RelatedAccountsSynchronizer(relatedRequest, relatedCache, accountSettings)
        val result = sync.synchronize()

        assertEquals(SyncResult.Success, result)
        
        val cache = relatedCache as FakeCache<List<Account>>
        // Verify we have 2 chunks: one of 200 and one of 5
        assertEquals(2, cache.cachedItems.size)
        assertEquals(200, cache.cachedItems[0].size)
        assertEquals(5, cache.cachedItems[1].size)
        

        assertEquals(largeAccountList, cache.cachedItems.flatten())
        
        assertTrue(sync.isSynchronized())
    }

    @Test
    fun `isSynchronized returns false initially`() = runTest {
        val sync = UserAccountSynchronizer(userRequest, userCache, accountSettings)
        assertTrue("Synchronizer should not be synchronized initially", !sync.isSynchronized())
    }

    @Test
    fun `RelatedAccountsSynchronizer handles empty list success`() = runTest {
        (relatedRequest as FakeRequestRelatedUserAccount).accountsOverride = emptyList()
        val sync = RelatedAccountsSynchronizer(relatedRequest, relatedCache, accountSettings)
        
        val result = sync.synchronize()
        
        assertEquals(SyncResult.Success, result)
        assertTrue(sync.isSynchronized())
    }

    @Test
    fun `UserAccountSynchronizer handles unexpected server exceptions`() = runTest {
        (userRequest as FakeAccountOwnerRequest).shouldThrowException = true
        val sync = UserAccountSynchronizer(userRequest, userCache, accountSettings)
        
        val result = sync.synchronize()
        
        assertTrue(result is SyncResult.Error.Unknown)
        assertEquals("Unexpected server error", (result as SyncResult.Error.Unknown).exception?.message)
        assertTrue(!sync.isSynchronized())
    }

    @Test
    fun `RelatedAccountsSynchronizer handles partial cache failure`() = runTest {
        val largeAccountList = List(205) { index ->
            Account(id = index.toString(), name = "User $index", email = "", phoneNumber = "", image = null, type = AccountUserType.WORKER)
        }
        (relatedRequest as FakeRequestRelatedUserAccount).accountsOverride = largeAccountList

        (relatedCache as FakeCache<List<Account>>).failOnCallIndex = 1
        
        val sync = RelatedAccountsSynchronizer(relatedRequest, relatedCache, accountSettings)
        val result = sync.synchronize()
        
        assertEquals(SyncResult.Error.DatabaseError(), result)
        // First chunk (200) was saved, second (5) failed.
        val cache = relatedCache as FakeCache<List<Account>>
        assertEquals(2, cache.cachedItems.size)
        assertTrue(!sync.isSynchronized())
    }

    @Test
    fun `UserAccountSynchronizer fails cache leads to error`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        (userCache as FakeCache<Account>).isCached = false
        val sync = UserAccountSynchronizer(userRequest, userCache, accountSettings)
        assertEquals(SyncResult.Error.DatabaseError(), sync.synchronize())
        assertTrue(!sync.isSynchronized())
    }

    @Test
    fun `UserAccountSynchronizer server response error`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        (userRequest as FakeAccountOwnerRequest).responseCode = 401
        val sync = UserAccountSynchronizer(userRequest, userCache, accountSettings)
        assertEquals(SyncResult.Error.Unauthorized("Error"), sync.synchronize())
        assertTrue(!sync.isSynchronized())
        (userRequest as FakeAccountOwnerRequest).responseCode = 500
        val sync1 = UserAccountSynchronizer(userRequest, userCache, accountSettings)
        assertEquals(SyncResult.Error.NetworkError(), sync1.synchronize())
        assertTrue(!sync1.isSynchronized())
    }

    @Test
    fun `UserAttendanceRapportSynchronizer delegates to cache and updates settings`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        val mockAccount = FakeData.Employee.account.toDBAccount(isUser = true).copy(id = 1L)
        `when`(accountDataApi.getAccountByServerId(anyString())).thenReturn(mockAccount)
        `when`(accountDataApi.addAttendances(anyObject())).thenReturn(true)

        val sync = UserAttendanceRapportSynchronizer(attendanceRequest, attendanceCache, accountSettings)
        val result = sync.synchronize()

        assertEquals(SyncResult.Success, result)
        assertTrue(accountSettings.isUserAttendanceRapportSynchronized.getData(false))
        assertTrue(sync.isSynchronized())
    }

    @Test
    fun `UserAttendanceRapportSynchronizer handles unexpected server exceptions`() = runTest {
        attendanceRequest.shouldThrowException = true
        val sync = UserAttendanceRapportSynchronizer(attendanceRequest, attendanceCache, accountSettings)

        val result = sync.synchronize()

        assertTrue(result is SyncResult.Error.Unknown)
        assertEquals("Unexpected server error", (result as SyncResult.Error.Unknown).exception?.message)
        assertTrue(!sync.isSynchronized())
    }

    @Test
    fun `UserAttendanceRapportSynchronizer fails cache leads to error`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        val mockAccount = FakeData.Employee.account.toDBAccount(isUser = true).copy(id = 1L)
        `when`(accountDataApi.getAccountByServerId(anyString())).thenReturn(mockAccount)
        `when`(accountDataApi.addAttendances(anyObject())).thenReturn(false)

        val sync = UserAttendanceRapportSynchronizer(attendanceRequest, attendanceCache, accountSettings)
        assertEquals(SyncResult.Error.DatabaseError(), sync.synchronize())
        assertTrue(!sync.isSynchronized())
    }

    @Test
    fun `UserAttendanceRapportSynchronizer server response error`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        attendanceRequest.responseCode = 401
        val sync = UserAttendanceRapportSynchronizer(attendanceRequest, attendanceCache, accountSettings)
        assertEquals(SyncResult.Error.Unauthorized("Error"), sync.synchronize())
        assertTrue(!sync.isSynchronized())

        attendanceRequest.responseCode = 500
        val sync1 = UserAttendanceRapportSynchronizer(attendanceRequest, attendanceCache, accountSettings)
        assertEquals(SyncResult.Error.NetworkError(), sync1.synchronize())
        assertTrue(!sync1.isSynchronized())
    }

    @Test
    fun `UserAttendanceRapportSynchronizer chunks large lists and updates settings`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        val largeAttendanceList = List(205) { index ->
            com.example.orgcore.internal.attendance.model.Attendance(
                id = index.toString(),
                serverAccountId = "acc_$index",
                checkIn = 1000L,
                checkOut = 2000L,
                date = 1000000L,
                attendanceStatus = com.example.orgcore.api.attendance.model.AttendanceStatus.PRESENT
            )
        }
        attendanceRequest.attendanceOverride = largeAttendanceList
        val mockAccount = FakeData.Employee.account.toDBAccount(isUser = true).copy(id = 1L)
        `when`(accountDataApi.getAccountByServerId(anyString())).thenReturn(mockAccount)
        `when`(accountDataApi.addAttendances(anyObject())).thenReturn(true)

        val sync = UserAttendanceRapportSynchronizer(attendanceRequest, attendanceCache, accountSettings)
        val result = sync.synchronize()

        assertEquals(SyncResult.Success, result)
        assertTrue(sync.isSynchronized())

        @Suppress("UNCHECKED_CAST")
        val captor = org.mockito.ArgumentCaptor.forClass(List::class.java) as org.mockito.ArgumentCaptor<List<DBAttendance>>
        org.mockito.Mockito.verify(accountDataApi, org.mockito.Mockito.times(2)).addAttendances(capture(captor))
        val capturedLists = captor.allValues
        assertEquals(2, capturedLists.size)
        assertEquals(200, capturedLists[0].size)
        assertEquals(5, capturedLists[1].size)
        assertEquals(205, capturedLists.flatten().size)
    }

}
