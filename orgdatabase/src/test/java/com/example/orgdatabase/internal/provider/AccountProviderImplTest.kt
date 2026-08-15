package com.example.orgdatabase.internal.provider

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orgdatabase.internal.dao.AccountDao
import com.example.orgdatabase.internal.dao.EmailDao
import com.example.orgdatabase.internal.dao.PhoneNumberDao
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import com.example.orgdatabase.internal.mapper.toDBAccount
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class AccountProviderImplTest {

    private lateinit var database: OrganisationDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var emailDao: EmailDao
    private lateinit var phoneNumberDao: PhoneNumberDao
    private lateinit var accountProvider: AccountProvider

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            OrganisationDatabase::class.java,
        ).allowMainThreadQueries().build()

        accountDao = database.accountDao()
        emailDao = database.emailDao()
        phoneNumberDao = database.phoneNumberDao()

        accountProvider = AccountProviderImpl(database, accountDao, emailDao, phoneNumberDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addAccountWithDetails_returnsTrue() = runTest {
        val dbAccount = createDBAccount("Test User", emails = listOf("test@example.com"), phoneNumbers = listOf("123456"))

        val result = accountProvider.addAccountWithDetails(dbAccount)

        assertTrue(result)
        // Verify via DAO as provider uses generated ID for internal operations
        val allAccounts = accountDao.observeAllAccounts().first()
        assertEquals(1, allAccounts.size)
        
        val accountId = allAccounts[0].id
        val fetched = accountProvider.getAccountWithDetailsById(accountId)
        assertNotNull(fetched)
        assertEquals("Test User", fetched?.name)
        assertEquals(1, fetched?.emails?.size)
        assertEquals("test@example.com", fetched?.emails?.get(0))
        assertEquals(1, fetched?.phoneNumbers?.size)
        assertEquals("123456", fetched?.phoneNumbers?.get(0))
    }

    @Test
    fun deleteAccountWithDetails_returnsTrue() = runTest {
        val accountId = accountDao.insert(createAccountEntity("Delete Me"))

        val result = accountProvider.deleteAccountWithDetails(accountId)

        assertTrue(result)
        val fetched = accountProvider.getAccountWithDetailsById(accountId)
        assertNull(fetched)
    }

    @Test
    fun updateAccount_returnsTrue() = runTest {
        accountDao.deleteAll()
        val accountId = accountDao.insert(createAccountEntity("Original Name"))
        val updatedAccount = createAccountEntity("Updated Name").copy(id = accountId)

        val result = accountProvider.updateAccount(updatedAccount.toDBAccount())

        assertTrue(result)
        val fetched = accountDao.getAccountById(accountId)
        assertEquals("Updated Name", fetched?.name)
    }

    @Test
    fun updateEmails_replacesOldEmails() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        emailDao.insert(EmailEntity(email = "old@test.com", accountId = accountId))

        val newEmails = listOf("new@test.com")
        val result = accountProvider.updateEmails(accountId, newEmails)

        assertTrue(result)
        val emails = emailDao.getEmailsForAccount(accountId).first()
        assertEquals(1, emails.size)
        assertEquals("new@test.com", emails[0].email)
    }

    @Test
    fun updatePhoneNumbers_replacesOldNumbers() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        phoneNumberDao.insert(PhoneNumberEntity(number = "111", accountId = accountId))

        val newNumbers = listOf("222")
        val result = accountProvider.updatePhoneNumbers(accountId, newNumbers)

        assertTrue(result)
        val numbers = phoneNumberDao.getPhoneNumbersForAccount(accountId).first()
        assertEquals(1, numbers.size)
        assertEquals("222", numbers[0].number)
    }

    @Test
    fun getAccountWithDetailsById_returnsCorrectData() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        emailDao.insert(EmailEntity(email = "email@test.com", accountId = accountId))

        val result = accountProvider.getAccountWithDetailsById(accountId)

        assertNotNull(result)
        assertEquals("User", result?.name)
        assertEquals(1, result?.emails?.size)
        assertEquals("email@test.com", result?.emails?.get(0))
    }

    @Test
    fun observeAccountWithDetailsById_emitsData() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        
        val result = accountProvider.observeAccountWithDetailsById(accountId).first()

        assertNotNull(result)
        assertEquals("User", result?.name)
    }

    @Test
    fun getAccountsByChunk_returnsCorrectPage() = runTest {
        repeat(5) { accountDao.insert(createAccountEntity("User $it")) }

        val result = accountProvider.getAccountsByChunk(page = 2, pageSize = 1)

        assertEquals(1, result.size)
        assertEquals("User 2", result[0].name)
    }

    @Test
    fun getAccountsByChunk_page0_returnsCorrectData() = runTest {
        repeat(5) { accountDao.insert(createAccountEntity("User $it")) }

        val result = accountProvider.getAccountsByChunk(page = 0, pageSize = 3)

        assertEquals(3, result.size)
        assertEquals("User 0", result[0].name)
        assertEquals("User 1", result[1].name)
        assertEquals("User 2", result[2].name)
    }

    @Test
    fun getAccountsPaged_returnsPagingSource() = runTest {
        accountDao.insert(createAccountEntity("Paged User"))

        val pagingSource = accountProvider.getAccountsPaged()
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(loadResult is PagingSource.LoadResult.Page)
        val page = loadResult as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("Paged User", page.data[0].name)
    }

    @Test
    fun addAccountsWithDetails_bulkInsertWorks() = runTest {
        val accounts = listOf(
            createDBAccount("User 1"),
            createDBAccount("User 2")
        )

        val result = accountProvider.addAccountsWitDetails(accounts)

        assertTrue(result)
        val all = accountDao.observeAllAccounts().first()
        assertEquals(2, all.size)
    }

    private fun createAccountEntity(name: String) = AccountEntity(
        name = name,
        photoUri = "uri_$name",
        isUser = true,
        accountType = AccountEntity.ACCOUNT_TYPE_BOSS,
        serverId = name
    )

    private fun createDBAccount(name: String, emails: List<String> = emptyList(), phoneNumbers: List<String> = emptyList()) = DBAccount(
        id = 0,
        name = name,
        photoUri = "uri_$name",
        isUser = true,
        accountType = AccountEntity.ACCOUNT_TYPE_BOSS,
        serverId = name,
        emails = emails,
        phoneNumbers = phoneNumbers
    )
}
