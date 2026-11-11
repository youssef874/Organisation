package com.example.orgdatabase.internal.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.AccountEntity
import com.example.orgdatabase.internal.entity.AttendanceEntity
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
    private lateinit var attendanceDao: AttendanceDao

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
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = 300L,
                attendanceStatus = AttendanceEntity.STATUS_PRESENT
            )
        )

        val detailsList = accountDao.getAllAccountsWithDetails().first()
        assertEquals(1, detailsList.size)
        val details = detailsList[0]
        assertEquals("User", details.account.name)
        assertEquals(1, details.emails.size)
        assertEquals("test@example.com", details.emails[0].email)
        assertEquals(1, details.phoneNumbers.size)
        assertEquals("123456", details.phoneNumbers[0].number)
        assertEquals(1, details.attendanceRecords.size)
        assertEquals(AttendanceEntity.STATUS_PRESENT, details.attendanceRecords[0].attendanceStatus)
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
    fun getAttendanceByAccountId() = runTest {
        val accountId = accountDao.insert(createAccount("AttendanceUser"))
        val id = attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 1000L,
                checkOut = 2000L,
                date = 10000L,
                attendanceStatus = AttendanceEntity.STATUS_PRESENT,
                requestReason = "None",
                isApproved = true,
                isRequestInProgress = false
            )
        )

        val records = attendanceDao.getAttendanceByAccountId(accountId)
        assertEquals(1, records.size)
        assertEquals(AttendanceEntity.STATUS_PRESENT, records[0].attendanceStatus)
        assertTrue(records[0].isApproved)
        assertEquals(id, records.first().id)
    }

    @Test
    fun observeAttendanceByAccountId() = runTest {
        val accountId = accountDao.insert(createAccount("ObserverUser"))
        val id = attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 1000L,
                checkOut = 2000L,
                date = 10000L,
                attendanceStatus = AttendanceEntity.STATUS_ABSENT,
                isApproved = false,
                isRequestInProgress = true
            )
        )

        val records = attendanceDao.observeAttendanceByAccountId(accountId).first()
        assertEquals(1, records.size)
        assertEquals(AttendanceEntity.STATUS_ABSENT, records[0].attendanceStatus)
        assertTrue(records[0].isRequestInProgress)
        assertEquals( id , records.first().id)
    }

    @Test
    fun getAttendanceByAccountAndDate() = runTest {
        val accountId = accountDao.insert(createAccount("DateUser"))
        val targetDate = 123456789L
        val  id = attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 1000L,
                checkOut = 2000L,
                date = targetDate,
                attendanceStatus = AttendanceEntity.STATUS_HOLIDAY
            )
        )

        val record = attendanceDao.getAttendanceByAccountAndDate(accountId, targetDate)
        assertNotNull(record)
        assertEquals(AttendanceEntity.STATUS_HOLIDAY, record?.attendanceStatus)
        assertEquals(id, record?.id)

        val missing = attendanceDao.getAttendanceByAccountAndDate(accountId, 999999L)
        assert(missing == null)
    }

    @Test
    fun updateAttendanceStatusDao() = runTest {
        val accountId = accountDao.insert(createAccount("UpdateStatusUser"))
        val targetDate = 1000L
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = targetDate,
                attendanceStatus = AttendanceEntity.STATUS_ABSENT
            )
        )

        val updatedRows = attendanceDao.updateAttendanceStatus(accountId, targetDate, AttendanceEntity.STATUS_PRESENT)
        assertEquals(1, updatedRows)

        val record = attendanceDao.getAttendanceByAccountAndDate(accountId, targetDate)
        assertEquals(AttendanceEntity.STATUS_PRESENT, record?.attendanceStatus)
    }

    @Test
    fun observeAttendanceByAccountAndDateDao() = runTest {
        val accountId = accountDao.insert(createAccount("ObserveDateUser"))
        val targetDate = 2000L
        attendanceDao.insert(
            AttendanceEntity(
                accountId = accountId,
                checkIn = 100L,
                checkOut = 200L,
                date = targetDate,
                attendanceStatus = AttendanceEntity.STATUS_PAID_LEAVE
            )
        )

        val record = attendanceDao.observeAttendanceByAccountAndDate(accountId, targetDate).first()
        assertNotNull(record)
        assertEquals(AttendanceEntity.STATUS_PAID_LEAVE, record?.attendanceStatus)
    }

    @Test
    fun getAndObserveAttendancesByDateRangeDao() = runTest {
        val accountId = accountDao.insert(createAccount("RangeUser"))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 100L, attendanceStatus = AttendanceEntity.STATUS_PRESENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 200L, attendanceStatus = AttendanceEntity.STATUS_PRESENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 300L, attendanceStatus = AttendanceEntity.STATUS_PRESENT))

        val list = attendanceDao.getAttendancesByDateRange(accountId, 150L, 250L)
        assertEquals(1, list.size)
        assertEquals(200L, list[0].date)

        val flowList = attendanceDao.observeAttendancesByDateRange(accountId, 50L, 350L).first()
        assertEquals(3, flowList.size)

        val chunked = attendanceDao.getAttendancesByDateRangeChunked(accountId, 50L, 350L, limit = 2, offset = 0)
        assertEquals(2, chunked.size)

        val paged = attendanceDao.getAttendancesByDateRangePaged(accountId, 50L, 350L)
        assertNotNull(paged)
    }

    @Test
    fun observeAttendancesByDateRangeChunkedDao() = runTest {
        val accountId = accountDao.insert(createAccount("RangeUser2"))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 100L, attendanceStatus = AttendanceEntity.STATUS_PRESENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 200L, attendanceStatus = AttendanceEntity.STATUS_PRESENT))

        val observeChunked = attendanceDao.observeAttendancesByDateRangeChunked(accountId, 50L, 250L, limit = 1, offset = 0).first()
        assertEquals(1, observeChunked.size)
        assertEquals(100L, observeChunked[0].date)
    }

    @Test
    fun getAttendanceCountByStatusAndDateRangeDao() = runTest {
        val accountId = accountDao.insert(createAccount("CountUser"))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 100L, attendanceStatus = AttendanceEntity.STATUS_ABSENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 200L, attendanceStatus = AttendanceEntity.STATUS_ABSENT))
        attendanceDao.insert(AttendanceEntity(accountId = accountId, checkIn = 0, checkOut = 0, date = 300L, attendanceStatus = AttendanceEntity.STATUS_PRESENT))

        val count = attendanceDao.getAttendanceCountByStatusAndDateRange(
            accountId = accountId,
            attendanceStatus = AttendanceEntity.STATUS_ABSENT,
            startDate = 50L,
            endDate = 250L
        )
        assertEquals(2, count)
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
