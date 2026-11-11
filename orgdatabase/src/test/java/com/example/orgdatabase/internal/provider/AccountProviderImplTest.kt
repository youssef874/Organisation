package com.example.orgdatabase.internal.provider

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orgdatabase.internal.dao.AccountDao
import com.example.orgdatabase.internal.dao.AttendanceDao
import com.example.orgdatabase.internal.dao.EmailDao
import com.example.orgdatabase.internal.dao.PhoneNumberDao
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.AttendanceEntity
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import com.example.orgdatabase.internal.mapper.toDBAccount
import com.example.orgdatabase.public.model.AttendanceStatusType
import com.example.orgdatabase.public.model.DBAccount
import com.example.orgdatabase.public.model.DBAttendance
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
    private lateinit var attendanceDao: AttendanceDao
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
        attendanceDao = database.attendanceDao()

        accountProvider = AccountProviderImpl(database, accountDao, emailDao, phoneNumberDao, attendanceDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addAccountWithDetails_returnsTrue() = runTest {
        val dbAccount = createDBAccount(
            "Test User",
            emails = listOf("test@example.com"),
            phoneNumbers = listOf("123456")
        )

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
        emailDao.insert(EmailEntity(email = "test@example.com", accountId = accountId))
        phoneNumberDao.insert(PhoneNumberEntity(number = "123456", accountId = accountId))
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 1000L,
                attendanceStatus = AttendanceStatusType.PRESENT
            )
        )

        val result = accountProvider.deleteAccountWithDetails(accountId)

        assertTrue(result)
        val fetched = accountProvider.getAccountWithDetailsById(accountId)
        assertNull(fetched)

        // Verify cascade deletion of related details
        val emails = emailDao.getEmailsForAccount(accountId).first()
        assertTrue(emails.isEmpty())

        val phones = phoneNumberDao.getPhoneNumbersForAccount(accountId).first()
        assertTrue(phones.isEmpty())

        val attendances = attendanceDao.getAttendanceByAccountId(accountId)
        assertTrue(attendances.isEmpty())
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
    fun updateAttendances_replacesOldAttendances() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 100L,
                attendanceStatus = AttendanceStatusType.ABSENT
            )
        )

        val newAttendances = listOf(
            DBAttendance(
                accountId = accountId,
                checkIn = 300L,
                checkOut = 400L,
                date = 200L,
                attendanceStatus = AttendanceStatusType.PRESENT,
                isApproved = true
            )
        )
        val result = accountProvider.updateAttendances(accountId, newAttendances)

        assertTrue(result)
        val records = attendanceDao.getAttendanceByAccountId(accountId)
        assertEquals(1, records.size)
        assertEquals(AttendanceStatusType.PRESENT, records[0].attendanceStatus)
        assertTrue(records[0].isApproved)
    }

    @Test
    fun getAttendancesByAccountId_returnsCorrectRecords() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 1000L,
                attendanceStatus = AttendanceStatusType.PRESENT
            )
        )

        val attendances = accountProvider.getAttendancesByAccountId(accountId)
        assertEquals(1, attendances.size)
        assertEquals(AttendanceStatusType.PRESENT, attendances[0].attendanceStatus)
    }

    @Test
    fun observeAttendancesByAccountId_emitsRecords() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 1000L,
                attendanceStatus = AttendanceStatusType.ABSENT
            )
        )

        val attendances = accountProvider.observeAttendancesByAccountId(accountId).first()
        assertEquals(1, attendances.size)
        assertEquals(AttendanceStatusType.ABSENT, attendances[0].attendanceStatus)
    }

    @Test
    fun updateAttendanceStatusProvider_returnsTrue() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        val date = 5000L
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = date,
                attendanceStatus = AttendanceStatusType.ABSENT
            )
        )

        val success = accountProvider.updateAttendanceStatus(accountId, date, AttendanceStatusType.PRESENT)
        assertTrue(success)

        val fetched = accountProvider.getAttendanceByDate(accountId, date)
        assertEquals(AttendanceStatusType.PRESENT, fetched?.attendanceStatus)
    }

    @Test
    fun getAndObserveAttendanceByDate_returnsCorrectRecord() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        val date = 6000L
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = date,
                attendanceStatus = AttendanceStatusType.HOLIDAY
            )
        )

        val fetched = accountProvider.getAttendanceByDate(accountId, date)
        assertNotNull(fetched)
        assertEquals(AttendanceStatusType.HOLIDAY, fetched?.attendanceStatus)

        val observed = accountProvider.observeAttendanceByDate(accountId, date).first()
        assertNotNull(observed)
        assertEquals(AttendanceStatusType.HOLIDAY, observed?.attendanceStatus)
    }

    @Test
    fun getAndObserveAttendancesByDateRangeProvider() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 100L, attendanceStatus = AttendanceStatusType.PRESENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 200L, attendanceStatus = AttendanceStatusType.PRESENT))

        val list = accountProvider.getAttendancesByDateRange(accountId, 50L, 150L)
        assertEquals(1, list.size)

        val flowList = accountProvider.observeAttendancesByDateRange(accountId, 50L, 250L).first()
        assertEquals(2, flowList.size)

        val chunked = accountProvider.getAttendancesByDateRangeChunked(accountId, 50L, 250L, 0, 1)
        assertEquals(1, chunked.size)

        val paged = accountProvider.getAttendancesByDateRangePaged(accountId, 50L, 250L)
        assertNotNull(paged)
    }

    @Test
    fun observeAttendancesByDateRangeChunked_returnsCorrectChunk() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 100L, attendanceStatus = AttendanceStatusType.PRESENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 200L, attendanceStatus = AttendanceStatusType.PRESENT))

        val observedChunk = accountProvider.observeAttendancesByDateRangeChunked(accountId, 50L, 250L, page = 0, pageSize = 1).first()
        assertEquals(1, observedChunk.size)
        assertEquals(100L, observedChunk[0].date)
    }

    @Test
    fun getAttendancesByAccountIdChunked_returnsCorrectChunk() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        repeat(5) {
            attendanceDao.insert(
                AttendanceEntity(
                    accountId = accountId,
                    checkIn = 100L,
                    checkOut = 200L,
                    date = (1000 + it).toLong(),
                    attendanceStatus = AttendanceStatusType.PRESENT
                )
            )
        }

        val chunk = accountProvider.getAttendancesByAccountIdChunked(accountId, page = 1, pageSize = 2)
        assertEquals(2, chunk.size)
    }

    @Test
    fun getAttendancesPaged_returnsPagingSource() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 1000L,
                attendanceStatus = AttendanceStatusType.PRESENT
            )
        )

        val pagingSource = accountProvider.getAttendancesPaged(accountId)
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
    }

    @Test
    fun getAccountWithDetailsById_returnsCorrectData() = runTest {
        val accountId = accountDao.insert(createAccountEntity("User"))
        emailDao.insert(EmailEntity(email = "email@test.com", accountId = accountId))
        phoneNumberDao.insert(PhoneNumberEntity(number = "987654", accountId = accountId))

        val result = accountProvider.getAccountWithDetailsById(accountId)

        assertNotNull(result)
        assertEquals("User", result?.name)
        assertEquals(1, result?.emails?.size)
        assertEquals("email@test.com", result?.emails?.get(0))
        assertEquals(1, result?.phoneNumbers?.size)
        assertEquals("987654", result?.phoneNumbers?.get(0))
    }

    @Test
    fun observeAccountWithDetailsById_emitsData() = runTest {
        val accountId = accountDao.insert(createAccountEntity("FlowUser"))
        emailDao.insert(EmailEntity(email = "flow@test.com", accountId = accountId))
        phoneNumberDao.insert(PhoneNumberEntity(number = "555444", accountId = accountId))

        val result = accountProvider.observeAccountWithDetailsById(accountId).first()

        assertNotNull(result)
        assertEquals("FlowUser", result?.name)
        assertEquals(1, result?.emails?.size)
        assertEquals("flow@test.com", result?.emails?.get(0))
        assertEquals(1, result?.phoneNumbers?.size)
        assertEquals("555444", result?.phoneNumbers?.get(0))
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

    @Test
    fun getAttendanceCountByStatusAndDateRange_returnsCorrectCount() = runTest {
        val accountId = accountDao.insert(createAccountEntity("AttendanceUser"))
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 1000L,
                attendanceStatus = AttendanceStatusType.ABSENT
            )
        )
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 2000L,
                attendanceStatus = AttendanceStatusType.ABSENT
            )
        )
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 3000L,
                attendanceStatus = AttendanceStatusType.PRESENT
            )
        )

        val absentCount = accountProvider.getAttendanceCountByStatusAndDateRange(
            accountId = accountId,
            attendanceStatus = AttendanceStatusType.ABSENT,
            startDate = 500L,
            endDate = 2500L
        )

        assertEquals(2, absentCount)
    }

    @Test
    fun addAttendance_insertsRecordSuccessfully() = runTest {
        val accountId = accountDao.insert(createAccountEntity("AttendanceUser"))
        val dbAttendance = DBAttendance(
            accountId = accountId,
            checkIn = 100L,
            checkOut = 200L,
            date = 1000L,
            attendanceStatus = AttendanceStatusType.PRESENT,
            isApproved = true
        )

        val result = accountProvider.addAttendance(dbAttendance)
        assertTrue(result)

        val records = attendanceDao.getAttendanceByAccountId(accountId)
        assertEquals(1, records.size)
        assertEquals(AttendanceStatusType.PRESENT, records[0].attendanceStatus)
        assertTrue(records[0].isApproved)
    }

    @Test
    fun addAttendances_batchInsertsRecordsSuccessfully() = runTest {
        val accountId = accountDao.insert(createAccountEntity("BatchAttendanceUser"))
        val attendances = listOf(
            DBAttendance(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 1000L,
                attendanceStatus = AttendanceStatusType.PRESENT
            ),
            DBAttendance(
                accountId = accountId,
                checkIn = 300L,
                checkOut = 400L,
                date = 2000L,
                attendanceStatus = AttendanceStatusType.ABSENT
            )
        )

        val result = accountProvider.addAttendances(attendances)
        assertTrue(result)

        val records = attendanceDao.getAttendanceByAccountId(accountId)
        assertEquals(2, records.size)
    }

    private fun createAccountEntity(name: String) = AccountEntity(
        name = name,
        photoUri = "uri_$name",
        isUser = true,
        accountType = AccountEntity.ACCOUNT_TYPE_BOSS,
        serverId = name
    )

    private fun createDBAccount(
        name: String,
        emails: List<String> = emptyList(),
        phoneNumbers: List<String> = emptyList()
    ) = DBAccount(
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
