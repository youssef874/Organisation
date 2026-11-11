package com.example.orgdatabase.internal.provider

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.example.orgdatabase.LoggerHelper
import com.example.orgdatabase.internal.dao.AccountDao
import com.example.orgdatabase.internal.dao.AttendanceDao
import com.example.orgdatabase.internal.dao.EmailDao
import com.example.orgdatabase.internal.dao.PhoneNumberDao
import com.example.orgdatabase.internal.db.OrganisationDatabase
import com.example.orgdatabase.internal.entity.EmailEntity
import com.example.orgdatabase.internal.entity.PhoneNumberEntity
import com.example.orgdatabase.internal.mapper.extractEmails
import com.example.orgdatabase.internal.mapper.extractPhoneNumbers
import com.example.orgdatabase.internal.mapper.toAccountEntity
import com.example.orgdatabase.internal.mapper.toAttendanceEntity
import com.example.orgdatabase.internal.mapper.toDBAccount
import com.example.orgdatabase.internal.mapper.toDBAttendance
import com.example.orgdatabase.public.model.DBAccount
import com.example.orgdatabase.public.model.DBAttendance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AccountProviderImpl(
    private val db: OrganisationDatabase,
    private val accountDao: AccountDao,
    private val emailDao: EmailDao,
    private val phoneNumberDao: PhoneNumberDao,
    private val attendanceDao: AttendanceDao
) : AccountProvider {

    companion object{
        private const val CLASS_NAME = "AccountProviderImpl"
        private const val TAG = "Account"
    }

    override suspend fun addAccountWithDetails(
        dbAccount: DBAccount
    ): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME addAccountWithDetails id: ${dbAccount.accountType}, emails: ${dbAccount.emails
        }, phoneNumbers: ${dbAccount.phoneNumbers}")
        return try {
            db.withTransaction {
                val accountId = accountDao.upsert(dbAccount.toAccountEntity())
                if (accountId != -1L) {
                    emailDao.deleteEmailsByAccountId(accountId)
                    phoneNumberDao.deletePhoneNumbersByAccountId(accountId)
                    emailDao.insert(dbAccount.extractEmails(accountId))
                    phoneNumberDao.insert(dbAccount.extractPhoneNumbers(accountId))
                    LoggerHelper.logger.d(
                        TAG,
                        "$CLASS_NAME addAccountWithDetails new account id: $accountId"
                    )
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addAccountWithDetails exception: ${e.message}")
            false
        }
    }

    override suspend fun addAccountsWitDetails(accounts: List<DBAccount>): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|addAccountsWitDetails| size: ${accounts.size}"
        )
        return try {
            db.withTransaction {
                accounts.all { account ->
                    val accountId = accountDao.upsert(account.toAccountEntity())
                    if (accountId != -1L) {
                        emailDao.deleteEmailsByAccountId(accountId)
                        phoneNumberDao.deletePhoneNumbersByAccountId(accountId)
                        emailDao.insert(account.extractEmails(accountId))
                        phoneNumberDao.insert(account.extractPhoneNumbers(accountId))
                        true
                    } else {
                        false
                    }
                }
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addAccountsWitDetails exception: ${e.message}")
            false
        }
    }

    override suspend fun deleteAccountWithDetails(accountId: Long): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME deleteAccountWithDetails id: $accountId")
        return try {
            val account = accountDao.getAccountById(accountId)
            if (account != null) {
                accountDao.delete(account).let {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME deleteAccountWithDetails deleted: $it")
                    it > 0
                }
            } else {
                LoggerHelper.logger.w(TAG, "$CLASS_NAME deleteAccountWithDetails account not found")
                false
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME deleteAccountWithDetails exception: ${e.message}")
            false
        }
    }

    override suspend fun updateAccount(account: DBAccount): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updateAccount id: ${account.id}")
        return try {
            accountDao.update(account.toAccountEntity().copy(id = account.id)).let {
                LoggerHelper.logger.d(TAG, "$CLASS_NAME updateAccount updated: $it")
                it > 0
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updateAccount exception: ${e.message}")
            false
        }
    }

    override suspend fun updateEmails(accountId: Long, emails: List<String>): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updateEmails id: $accountId, emails: ${emails.size}")
        return try {
            db.withTransaction {
                emailDao.deleteEmailsByAccountId(accountId)
                emailDao.insert(emails.map { EmailEntity(email = it, accountId = accountId) }).let {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME updateEmails inserted: $it")
                    it.size == emails.size
                }
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updateEmails exception: ${e.message}")
            false
        }
    }

    override suspend fun updatePhoneNumbers(accountId: Long, phoneNumbers: List<String>): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updatePhoneNumbers id: $accountId, phoneNumbers: ${phoneNumbers.size}")
        return try {
            db.withTransaction {
                phoneNumberDao.deletePhoneNumbersByAccountId(accountId)
                phoneNumberDao.insert(phoneNumbers.map { PhoneNumberEntity(number = it, accountId = accountId) }).let {
                    LoggerHelper.logger.d(TAG, "$CLASS_NAME updatePhoneNumbers inserted: $it")
                    it.size == phoneNumbers.size
                }
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updatePhoneNumbers exception: ${e.message}")
            false
        }
    }

    override suspend fun updateAttendances(
        accountId: Long,
        attendances: List<DBAttendance>
    ): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updateAttendances id: $accountId, attendances: ${attendances.size}")
        return try {
            db.withTransaction {
                attendanceDao.deleteAttendanceByAccountId(accountId)
                val entities = attendances.map { it.copy(accountId = accountId).toAttendanceEntity() }
                val insertedIds = attendanceDao.insert(entities)
                insertedIds.size == attendances.size
            }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updateAttendances exception: ${e.message}")
            false
        }
    }

    override suspend fun addAttendance(attendance: DBAttendance): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME addAttendance accountId: ${attendance.accountId}")
        return try {
            val entity = attendance.toAttendanceEntity()
            val id = attendanceDao.upsert(entity)
            id != -1L
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addAttendance exception: ${e.message}")
            false
        }
    }

    override suspend fun addAttendances(attendances: List<DBAttendance>): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME addAttendances size: ${attendances.size}")
        return try {
            val entities = attendances.map { it.toAttendanceEntity() }
            val results = attendanceDao.upsert(entities)
            results.all { it != -1L }
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME addAttendances exception: ${e.message}")
            false
        }
    }

    override suspend fun updateAttendanceStatus(accountId: Long, date: Long, status: String): Boolean {
        LoggerHelper.logger.d(TAG, "$CLASS_NAME updateAttendanceStatus id: $accountId, date: $date, status: $status")
        return try {
            attendanceDao.updateAttendanceStatus(accountId, date, status) > 0
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME updateAttendanceStatus exception: ${e.message}")
            false
        }
    }

    override suspend fun getAttendancesByAccountId(accountId: Long): List<DBAttendance> {
        return attendanceDao.getAttendanceByAccountId(accountId).map { it.toDBAttendance() }
    }

    override fun observeAttendancesByAccountId(accountId: Long): Flow<List<DBAttendance>> {
        return attendanceDao.observeAttendanceByAccountId(accountId).map { list -> list.map { it.toDBAttendance() } }
    }

    override suspend fun getAttendanceByDate(accountId: Long, date: Long): DBAttendance? {
        return attendanceDao.getAttendanceByAccountAndDate(accountId, date)?.toDBAttendance()
    }

    override fun observeAttendanceByDate(accountId: Long, date: Long): Flow<DBAttendance?> {
        return attendanceDao.observeAttendanceByAccountAndDate(accountId, date).map { it?.toDBAttendance() }
    }

    override suspend fun getAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): List<DBAttendance> {
        return attendanceDao.getAttendancesByDateRange(accountId, startDate, endDate).map { it.toDBAttendance() }
    }

    override suspend fun getAttendanceCountByStatusAndDateRange(
        accountId: Long,
        attendanceStatus: String,
        startDate: Long,
        endDate: Long
    ): Int {
        return try {
            LoggerHelper.logger.d(
                TAG,
                "$CLASS_NAME getAttendanceCountByStatusAndDateRange id: $accountId, status: $attendanceStatus, startDate: $startDate, endDate: $endDate"
            )
            attendanceDao.getAttendanceCountByStatusAndDateRange(accountId, attendanceStatus, startDate, endDate)
        } catch (e: Exception) {
            LoggerHelper.logger.e(TAG, "$CLASS_NAME getAttendanceCountByStatusAndDateRange exception: ${e.message}")
            0
        }
    }

    override fun observeAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): Flow<List<DBAttendance>> {
        return attendanceDao.observeAttendancesByDateRange(accountId, startDate, endDate).map { list -> list.map { it.toDBAttendance() } }
    }

    override suspend fun getAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, page: Int, pageSize: Int): List<DBAttendance> {
        return attendanceDao.getAttendancesByDateRangeChunked(accountId, startDate, endDate, pageSize, pageSize * page).map { it.toDBAttendance() }
    }

    override fun observeAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, page: Int, pageSize: Int): Flow<List<DBAttendance>> {
        return attendanceDao.observeAttendancesByDateRangeChunked(accountId, startDate, endDate, pageSize, pageSize * page).map { list -> list.map { it.toDBAttendance() } }
    }

    override fun getAttendancesByDateRangePaged(accountId: Long, startDate: Long, endDate: Long): PagingSource<Int, DBAttendance> {
        return MappedPagingSource(
            attendanceDao.getAttendancesByDateRangePaged(accountId, startDate, endDate)
        ) { it.toDBAttendance() }
    }

    override suspend fun getAttendancesByAccountIdChunked(accountId: Long, page: Int, pageSize: Int): List<DBAttendance> {
        return attendanceDao.getAttendanceByAccountIdChunked(accountId, pageSize, pageSize * page)
            .map { it.toDBAttendance() }
    }

    override fun getAttendancesPaged(accountId: Long): PagingSource<Int, DBAttendance> {
        return MappedPagingSource(
            attendanceDao.getAttendanceByAccountIdPaged(accountId)
        ) { it.toDBAttendance() }
    }

    override suspend fun getAccountWithDetailsById(accountId: Long): DBAccount? {
        return accountDao.getAccountWithDetailsByIdSuspend(accountId)?.toDBAccount()
    }

    override suspend fun getAccountByServerId(serverId: String): DBAccount? {
        return accountDao.getAccountWithDetailsByServerIdSuspend(serverId)?.toDBAccount()
    }

    override suspend fun getUserAccount(): DBAccount? {
        return accountDao.getUserAccountWithDetailsSuspend()?.toDBAccount()
    }

    override suspend fun getAttendanceByServerId(serverId: String): DBAttendance? {
        return attendanceDao.getAttendanceByServerId(serverId)?.toDBAttendance()
    }

    override fun observeAccountWithDetailsById(accountId: Long): Flow<DBAccount?> {
        return accountDao.getAccountWithDetailsById(accountId).map { it?.toDBAccount() }
    }

    override suspend fun getAccountsByChunk(page: Int, pageSize: Int): List<DBAccount> {
        return accountDao.getAccountsByChunk(pageSize, pageSize*page).map { it.toDBAccount() }
    }

    override fun getAccountsPaged(): PagingSource<Int, DBAccount> {
        return MappedPagingSource(
            accountDao.getAccountsWithDetailsPaged()
        ) { it.toDBAccount() }
    }
}

private class MappedPagingSource<Key : Any, Value : Any, NewValue : Any>(
    private val originalSource: PagingSource<Key, Value>,
    private val mapper: (Value) -> NewValue
) : PagingSource<Key, NewValue>() {

    override fun getRefreshKey(state: PagingState<Key, NewValue>): Key? {
        return null
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, NewValue> {
        return when (val result = originalSource.load(params)) {
            is LoadResult.Page -> LoadResult.Page(
                data = result.data.map(mapper),
                prevKey = result.prevKey,
                nextKey = result.nextKey,
                itemsBefore = result.itemsBefore,
                itemsAfter = result.itemsAfter
            )
            is LoadResult.Error -> LoadResult.Error(result.throwable)
            is LoadResult.Invalid -> LoadResult.Invalid()
        }
    }

    override val jumpingSupported: Boolean get() = originalSource.jumpingSupported
    override val keyReuseSupported: Boolean get() = originalSource.keyReuseSupported

    init {
        originalSource.registerInvalidatedCallback { invalidate() }
    }
}
