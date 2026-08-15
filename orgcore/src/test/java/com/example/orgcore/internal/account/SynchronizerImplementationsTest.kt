package com.example.orgcore.internal.account

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.commun.OfflineCache
import com.example.orgcore.commun.ServerRequest
import com.example.orgcore.commun.SyncResult
import com.example.orgcore.internal.FakeCache
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.Authentication.provider.FakeAuthenticationProvider
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.account.request.FakeAccountOwnerRequest
import com.example.orgcore.internal.account.request.FakeRequestRelatedUserAccount
import com.example.orgcore.internal.account.sync.RelatedAccountsSynchronizer
import com.example.orgcore.internal.account.sync.UserAccountSynchronizer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userRequest = FakeAccountOwnerRequest(
            authenticationProvider
        )
        relatedRequest = FakeRequestRelatedUserAccount(authenticationProvider)
        userCache = FakeCache()
        relatedCache = FakeCache()
    }

    @Test
    fun `UserAccountSynchronizer delegates to cache and updates settings`() = runTest {
        authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(
            AccountUserType.WORKER
        )
        val account = Account(
            id = "1",
            name = "WORKER",
            email = "worker@gmail.com",
            phoneNumber = "12345678",
            image = null,
            type = AccountUserType.WORKER
        )


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

}
