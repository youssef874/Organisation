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
class UserAccountCacheTest {

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    private lateinit var userAccountCache: UserAccountCache

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userAccountCache = UserAccountCache(accountDataApi)
    }

    @Test
    fun `cache maps account correctly and calls database with isUser true`() = runTest {
        val account = FakeData.Manager.account.copy(image = "photo_uri")
        `when`(accountDataApi.addAccountWithDetails(anyObject())).thenReturn(true)

        val result = userAccountCache.cache(account)

        assertTrue(result)
        val captor = ArgumentCaptor.forClass(DBAccount::class.java) as ArgumentCaptor<DBAccount>
        verify(accountDataApi).addAccountWithDetails(capture(captor))
        
        val dbAccount = captor.value
        assertEquals(account.id, dbAccount.serverId)
        assertEquals(account.name, dbAccount.name)
        assertEquals(listOf(account.email), dbAccount.emails)
        assertEquals(listOf(account.phoneNumber), dbAccount.phoneNumbers)
        assertEquals(account.image, dbAccount.photoUri)
        assertEquals(account.type, dbAccount.accountType)
        assertTrue(dbAccount.isUser)
    }

    @Test
    fun `cache returns false when database call fails`() = runTest {
        val account = FakeData.Employee.account.copy(email = null, phoneNumber = null, image = null)
        `when`(accountDataApi.addAccountWithDetails(anyObject())).thenReturn(false)

        val result = userAccountCache.cache(account)

        assertTrue(!result)
    }
}
