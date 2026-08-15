package com.example.orgdatabase.internal.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class OrganisationDaoTest {

    private lateinit var database: OrganisationDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var emailDao: EmailDao
    private lateinit var phoneNumberDao: PhoneNumberDao

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
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAccount() = runTest {
        val account = createAccount("Admin")
        val id = accountDao.insert(account)
        
        val fetched = accountDao.getAccountById(id)
        assertNotNull(fetched)
        assertEquals("Admin", fetched?.name)
        assertEquals("Admin", fetched?.serverId)
    }

    @Test
    fun getAllAccountsWithDetailsFlow() = runTest {
        val accountId = accountDao.insert(createAccount("User"))
        emailDao.insert(EmailEntity(email = "test@example.com", accountId = accountId))
        phoneNumberDao.insert(PhoneNumberEntity(number = "123456", accountId = accountId))

        val detailsList = accountDao.getAllAccountsWithDetails().first()
        assertEquals(1, detailsList.size)
        val details = detailsList[0]
        assertEquals("User", details.account.name)
        assertEquals(1, details.emails.size)
        assertEquals("test@example.com", details.emails[0].email)
        assertEquals(1, details.phoneNumbers.size)
        assertEquals("123456", details.phoneNumbers[0].number)
    }

    @Test
    fun getAccountWithDetailsByIdSuspend() = runTest {
        val accountId = accountDao.insert(createAccount("DetailsUser"))
        emailDao.insert(EmailEntity(email = "details@example.com", accountId = accountId))
        
        val details = accountDao.getAccountWithDetailsByIdSuspend(accountId)
        assertNotNull(details)
        assertEquals("DetailsUser", details?.account?.name)
        assertEquals(1, details?.emails?.size)
    }

    @Test
    fun getAccountsByChunk() = runTest {
        repeat(5) { accountDao.insert(createAccount("User $it")) }
        
        val chunk = accountDao.getAccountsByChunk(limit = 2, offset = 1)
        assertEquals(2, chunk.size)
        // Offset 1 skip "User 0", get "User 1" and "User 2"
        assertEquals("User 1", chunk[0].name)
        assertEquals("User 2", chunk[1].name)
    }

    @Test
    fun getAccountsByChunkFlow() = runTest {
        accountDao.insert(createAccount("First"))
        
        val flow = accountDao.getAccountsByChunkFlow(limit = 1, offset = 0)
        val firstChunk = flow.first()
        assertEquals(1, firstChunk.size)
        assertEquals("First", firstChunk[0].name)
    }

    @Test
    fun getEmailsForAccountFlow() = runTest {
        val accountId = accountDao.insert(createAccount("EmailUser"))
        emailDao.insert(EmailEntity(email = "e1@test.com", accountId = accountId))
        emailDao.insert(EmailEntity(email = "e2@test.com", accountId = accountId))

        val emails = emailDao.getEmailsForAccount(accountId).first()
        assertEquals(2, emails.size)
    }

    @Test
    fun getPhoneNumbersForAccountFlow() = runTest {
        val accountId = accountDao.insert(createAccount("PhoneUser"))
        phoneNumberDao.insert(PhoneNumberEntity(number = "000", accountId = accountId))

        val numbers = phoneNumberDao.getPhoneNumbersForAccount(accountId).first()
        assertEquals(1, numbers.size)
        assertEquals("000", numbers[0].number)
    }

    @Test
    fun getAccountCount() = runTest {
        val initialCount = accountDao.getAccountCount()
        assertEquals(0, initialCount)

        accountDao.insert(createAccount("User 1"))
        accountDao.insert(createAccount("User 2"))

        val finalCount = accountDao.getAccountCount()
        assertEquals(2, finalCount)
    }

    @Test
    fun getUserAccount() = runTest {
        // Initially null
        assert(accountDao.getUserAccount() == null)

        // Insert non-user
        accountDao.insert(createAccount("NotUser", isUser = false))
        assert(accountDao.getUserAccount() == null)

        // Insert user
        val user = createAccount("TheUser", isUser = true)
        accountDao.insert(user)
        
        val fetched = accountDao.getUserAccount()
        assertNotNull(fetched)
        assertEquals("TheUser", fetched?.name)
        assertEquals(true, fetched?.isUser)
    }

    @Test
    fun getUserAccountFlow() = runTest {
        val user = createAccount("FlowUser", isUser = true)
        accountDao.insert(user)
        
        val flow = accountDao.getUserAccountFlow()
        assertEquals("FlowUser", flow.first()?.name)
    }

    @Test
    fun getAllAssignedAccounts() = runTest {
        val supervisorId = 100L
        accountDao.insert(createAccount("Sub 1", supervisorId = supervisorId))
        accountDao.insert(createAccount("Sub 2", supervisorId = supervisorId))
        accountDao.insert(createAccount("Other", supervisorId = 200L))

        val assigned = accountDao.getAllAssignedAccounts(supervisorId)
        assertEquals(2, assigned.size)
        assertTrue(assigned.all { it.supervisorId == supervisorId })
    }

    @Test
    fun getAllAssignedAccountsFlow() = runTest {
        val supervisorId = 300L
        accountDao.insert(createAccount("Worker", supervisorId = supervisorId))
        
        val assignedList = accountDao.getAllAssignedAccountsFlow(supervisorId).first()
        assertEquals(1, assignedList.size)
        assertEquals("Worker", assignedList[0].name)
    }

    @Test
    fun getAllAssignedAccountsChunked() = runTest {
        val supervisorId = 400L
        repeat(5) {
            accountDao.insert(createAccount("Sub $it", supervisorId = supervisorId))
        }

        val chunk = accountDao.getAllAssignedAccountsChunked(supervisorId, limit = 2, offset = 1)
        assertEquals(2, chunk.size)
        assertEquals("Sub 1", chunk[0].name)
        assertEquals("Sub 2", chunk[1].name)
    }

    private fun createAccount(
        name: String, 
        isUser: Boolean = false, 
        supervisorId: Long? = null
    ) = AccountEntity(
        name = name,
        photoUri = "uri_$name",
        isUser = isUser,
        accountType = AccountEntity.ACCOUNT_TYPE_BOSS,
        serverId = name,
        supervisorId = supervisorId
    )
}
