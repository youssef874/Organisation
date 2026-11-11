package com.example.orgcore.internal.account.cache

import com.example.orgcore.commun.FakeData
import com.example.orgcore.commun.anyObject
import com.example.orgcore.commun.capture
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RelatedUserAccountCacheTest {

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    private lateinit var relatedUserAccountCache: RelatedUserAccountCache

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        relatedUserAccountCache = RelatedUserAccountCache(accountDataApi)
    }

    @Test
    fun `cache maps related accounts correctly and calls database with isUser false`() = runTest {
        val accounts = FakeData.Boss.relatedAccount
        `when`(accountDataApi.addAccountsWithDetails(anyObject())).thenReturn(true)

        val result = relatedUserAccountCache.cache(accounts)

        assertTrue(result)
        val captor = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<DBAccount>>
        verify(accountDataApi).addAccountsWithDetails(capture(captor))
        
        val dbAccounts = captor.value
        assertEquals(accounts.size, dbAccounts.size)
        
        dbAccounts.forEachIndexed { index, dbAccount ->
            assertEquals(accounts[index].id, dbAccount.serverId)
            assertTrue(!dbAccount.isUser)
        }
    }

    @Test
    fun `cache returns false when database call fails`() = runTest {
        `when`(accountDataApi.addAccountsWithDetails(anyObject())).thenReturn(false)

        val result = relatedUserAccountCache.cache(emptyList())

        assertTrue(!result)
    }
}
