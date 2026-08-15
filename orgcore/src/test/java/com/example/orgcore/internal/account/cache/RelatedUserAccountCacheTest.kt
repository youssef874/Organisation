package com.example.orgcore.internal.account.cache

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.commun.anyObject
import com.example.orgcore.commun.capture
import com.example.orgcore.internal.account.model.Account
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

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
        val accounts = listOf(
            Account(
                id = "id1",
                name = "Worker 1",
                email = "w1@example.com",
                phoneNumber = "111",
                image = null,
                type = AccountUserType.WORKER
            ),
            Account(
                id = "id2",
                name = "Boss",
                email = "boss@example.com",
                phoneNumber = "000",
                image = "boss_uri",
                type = AccountUserType.BOSS
            )
        )
        `when`(accountDataApi.addAccountsWithDetails(anyObject())).thenReturn(true)

        val result = relatedUserAccountCache.cache(accounts)

        assertTrue(result)
        val captor = ArgumentCaptor.forClass(List::class.java) as ArgumentCaptor<List<DBAccount>>
        verify(accountDataApi).addAccountsWithDetails(capture(captor))
        
        val dbAccounts = captor.value
        assertEquals(2, dbAccounts.size)
        
        assertEquals("id1", dbAccounts[0].serverId)
        assertTrue(!dbAccounts[0].isUser)
        
        assertEquals("id2", dbAccounts[1].serverId)
        assertEquals("boss_uri", dbAccounts[1].photoUri)
        assertTrue(!dbAccounts[1].isUser)
    }

    @Test
    fun `cache returns false when database call fails`() = runTest {
        `when`(accountDataApi.addAccountsWithDetails(anyObject())).thenReturn(false)

        val result = relatedUserAccountCache.cache(emptyList())

        assertTrue(!result)
    }
}
