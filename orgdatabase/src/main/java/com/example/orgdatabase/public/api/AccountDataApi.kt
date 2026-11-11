package com.example.orgdatabase.public.api

import android.content.Context
import androidx.paging.PagingSource
import com.example.orgdatabase.internal.DataProvider
import com.example.orgdatabase.public.model.AttendanceStatusType
import com.example.orgdatabase.public.model.DBAccount
import com.example.orgdatabase.public.model.DBAttendance
import kotlinx.coroutines.flow.Flow


/**
 * Api to retrieve all cashed data related to Account
 */
class AccountDataApi(private val context: Context) {


    /**
     * Add new account to database
     * @param dbAccount: The account to be added [DBAccount]
     * @return true if the account added successfully otherwise return false
     */
    suspend fun addAccountWithDetails(dbAccount: DBAccount): Boolean =
        DataProvider.getAccountProvider(context).addAccountWithDetails(
            dbAccount
        )

    /**
     * Add List of account to database
     * @param dbAccounts: The account list to be added [DBAccount]
     * @return true if the account list added successfully otherwise return false
     */
    suspend fun addAccountsWithDetails(dbAccounts: List<DBAccount>): Boolean =
        DataProvider.getAccountProvider(context).addAccountsWitDetails(dbAccounts)

    /**
     * Delete account from database
     * @param accountId: The account id to be removed this will
     * include all related data emails, phone number ...
     * @return true if the remove is success otherwise return false
     */
    suspend fun deleteAccountWithDetails(accountId: Long): Boolean =
        DataProvider.getAccountProvider(context).deleteAccountWithDetails(accountId)

    /**
     * update the account with the new data
     * @param account: new [DBAccount] instance contain all the changes
     * @return true if the update was successful
     */
    suspend fun updateAccount(account: DBAccount): Boolean =
        DataProvider.getAccountProvider(context).updateAccount(account)

    /**
     * Update the account emails
     * @param accountId: the id of the account emails to be updated
     * @param emails: list of the account emails to be updated
     * @return true if the update was success otherwise return false
     */
    suspend fun updateEmails(accountId: Long, emails: List<String>): Boolean =
        DataProvider.getAccountProvider(context).updateEmails(accountId, emails)

    /**
     * Update the account phone numbers
     * @param accountId: the id of the account phone numbers to be updated
     * @param phoneNumbers: list of the account phone numbers to be updated
     * @return true if the update was success otherwise return false
     */
    suspend fun updatePhoneNumbers(accountId: Long, phoneNumbers: List<String>): Boolean =
        DataProvider.getAccountProvider(context).updatePhoneNumbers(accountId, phoneNumbers)

    /**
     * Update the account attendance records
     * @param accountId: the id of the account attendance records to be updated
     * @param attendances: list of the account attendance records to be updated
     * @return true if the update was success otherwise return false
     */
    suspend fun updateAttendances(accountId: Long, attendances: List<DBAttendance>): Boolean =
        DataProvider.getAccountProvider(context).updateAttendances(accountId, attendances)

    /**
     * Adds a single attendance record.
     *
     * @param attendance The [DBAttendance] record to be added.
     * @return `true` if added successfully, `false` otherwise.
     */
    suspend fun addAttendance(attendance: DBAttendance): Boolean =
        DataProvider.getAccountProvider(context).addAttendance(attendance)

    /**
     * Adds a list of attendance records in a batch.
     *
     * @param attendances The list of [DBAttendance] records to be added.
     * @return `true` if all records were added successfully, `false` otherwise.
     */
    suspend fun addAttendances(attendances: List<DBAttendance>): Boolean =
        DataProvider.getAccountProvider(context).addAttendances(attendances)

    /**
     * Update the attendance status for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date for which to update the status.
     * @param status The new attendance status, restricted to valid values defined in [AttendanceStatusType].
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateAttendanceStatus(accountId: Long, date: Long, @AttendanceStatusType status: String): Boolean =
        DataProvider.getAccountProvider(context).updateAttendanceStatus(accountId, date, status)

    /**
     * Retrieves all attendance records for a specific account.
     *
     * @param accountId The ID of the account.
     * @return A list of [DBAttendance] records.
     */
    suspend fun getAttendancesByAccountId(accountId: Long): List<DBAttendance> =
        DataProvider.getAccountProvider(context).getAttendancesByAccountId(accountId)

    /**
     * Observes attendance records for a specific account.
     *
     * @param accountId The ID of the account to observe.
     * @return A [Flow] emitting a list of [DBAttendance] records whenever changes occur.
     */
    fun observeAttendancesByAccountId(accountId: Long): Flow<List<DBAttendance>> =
        DataProvider.getAccountProvider(context).observeAttendancesByAccountId(accountId)

    /**
     * Retrieves the attendance record for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date.
     * @return The [DBAttendance] record if found, or `null` otherwise.
     */
    suspend fun getAttendanceByDate(accountId: Long, date: Long): DBAttendance? =
        DataProvider.getAccountProvider(context).getAttendanceByDate(accountId, date)

    /**
     * Observes the attendance record for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date.
     * @return A [Flow] emitting the [DBAttendance] record or `null` whenever changes occur.
     */
    fun observeAttendanceByDate(accountId: Long, date: Long): Flow<DBAttendance?> =
        DataProvider.getAccountProvider(context).observeAttendanceByDate(accountId, date)

    /**
     * Retrieves attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return A list of [DBAttendance] records falling within the specified range.
     */
    suspend fun getAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): List<DBAttendance> =
        DataProvider.getAccountProvider(context).getAttendancesByDateRange(accountId, startDate, endDate)

    /**
     * Retrieves the count of attendance records for a specific account with a given attendance status within a date range
     * (e.g., how many days the account was absent or present in the specified range).
     *
     * @param accountId The ID of the account.
     * @param attendanceStatus The attendance status to filter by, restricted to valid values defined in [AttendanceStatusType].
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return The count of matching attendance records.
     */
    suspend fun getAttendanceCountByStatusAndDateRange(
        accountId: Long,
        @AttendanceStatusType attendanceStatus: String,
        startDate: Long,
        endDate: Long
    ): Int =
        DataProvider.getAccountProvider(context).getAttendanceCountByStatusAndDateRange(accountId, attendanceStatus, startDate, endDate)

    /**
     * Observes attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return A [Flow] emitting a list of [DBAttendance] records within the range whenever changes occur.
     */
    fun observeAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): Flow<List<DBAttendance>> =
        DataProvider.getAccountProvider(context).observeAttendancesByDateRange(accountId, startDate, endDate)

    /**
     * Retrieves a chunk (page) of attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A list of [DBAttendance] records for the requested chunk.
     */
    suspend fun getAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, page: Int, pageSize: Int): List<DBAttendance> =
        DataProvider.getAccountProvider(context).getAttendancesByDateRangeChunked(accountId, startDate, endDate, page, pageSize)

    /**
     * Observes a chunk (page) of attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A [Flow] emitting a list of [DBAttendance] records for the requested chunk whenever changes occur.
     */
    fun observeAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, page: Int, pageSize: Int): Flow<List<DBAttendance>> =
        DataProvider.getAccountProvider(context).observeAttendancesByDateRangeChunked(accountId, startDate, endDate, page, pageSize)

    /**
     * Returns a [PagingSource] for paginated access to attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return A [PagingSource] providing paged attendance records.
     */
    fun getAttendancesByDateRangePaged(accountId: Long, startDate: Long, endDate: Long): PagingSource<Int, DBAttendance> =
        DataProvider.getAccountProvider(context).getAttendancesByDateRangePaged(accountId, startDate, endDate)

    /**
     * Retrieves a chunk (page) of attendance records for a specific account.
     *
     * @param accountId The ID of the account.
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A list of [DBAttendance] records for the requested chunk.
     */
    suspend fun getAttendancesByAccountIdChunked(accountId: Long, page: Int, pageSize: Int): List<DBAttendance> =
        DataProvider.getAccountProvider(context).getAttendancesByAccountIdChunked(accountId, page, pageSize)

    /**
     * Returns a [PagingSource] for paginated access to attendance records for an account.
     *
     * @param accountId The ID of the account.
     * @return A [PagingSource] providing paged attendance records.
     */
    fun getAttendancesPaged(accountId: Long): PagingSource<Int, DBAttendance> =
        DataProvider.getAccountProvider(context).getAttendancesPaged(accountId)

    /**
     * return one shot account by id
     * @param accountId: the id of the account requested
     * @return the requested account in  [DBAccount] or null if the no cashed account by the requested id
     */
    suspend fun getAccountWithDetailsById(accountId: Long): DBAccount? =
        DataProvider.getAccountProvider(context).getAccountWithDetailsById(accountId)

    /**
     * return the account by id the observe any changes occurs to requested account
     * @param accountId: the id of the account requested
     * @return the requested account in [Flow] of [DBAccount] or null if the no cashed account by the requested id
     */
    fun observeAccountWithDetailsById(accountId: Long): Flow<DBAccount?> =
        DataProvider.getAccountProvider(context).observeAccountWithDetailsById(accountId)

    /**
     * get all accounts by chunk
     * @param page: the page number to be requested
     * @param pageSize: the number of accounts per page
     * @return the requested page of accounts in [List] of [DBAccount]
     */
    suspend fun getAccountsByChunk(page: Int, pageSize: Int): List<DBAccount> =
        DataProvider.getAccountProvider(context).getAccountsByChunk(page, pageSize)

    /**
     * Retrieves an account by its server ID.
     *
     * @param serverId The server ID of the account.
     * @return The [DBAccount] if found, or `null` otherwise.
     */
    suspend fun getAccountByServerId(serverId: String): DBAccount? =
        DataProvider.getAccountProvider(context).getAccountByServerId(serverId)

    /**
     * Retrieves the current user account.
     *
     * @return The [DBAccount] marked as user, or `null` if not found.
     */
    suspend fun getUserAccount(): DBAccount? =
        DataProvider.getAccountProvider(context).getUserAccount()

    /**
     * Retrieves an attendance record by its server ID.
     *
     * @param serverId The server ID of the attendance record.
     * @return The [DBAttendance] if found, or `null` otherwise.
     */
    suspend fun getAttendanceByServerId(serverId: String): DBAttendance? =
        DataProvider.getAccountProvider(context).getAttendanceByServerId(serverId)

}
