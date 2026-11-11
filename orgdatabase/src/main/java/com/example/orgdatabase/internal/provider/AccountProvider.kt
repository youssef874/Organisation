package com.example.orgdatabase.internal.provider

import androidx.paging.PagingSource
import com.example.orgdatabase.public.model.AttendanceStatusType
import com.example.orgdatabase.public.model.DBAccount
import com.example.orgdatabase.public.model.DBAttendance
import kotlinx.coroutines.flow.Flow

/**
 * Interface for providing account data from the local database.
 */
internal interface AccountProvider {

    /**
     * Adds a single account with its details (emails, phone numbers) to the database.
     *
     * @param dbAccount The account data to add.
     * @return `true` if successful, `false` otherwise.
     */
    suspend fun addAccountWithDetails(dbAccount: DBAccount): Boolean

    /**
     * Adds a list of accounts with their details to the database in a batch.
     *
     * @param accounts The list of accounts to add.
     * @return `true` if all accounts were added successfully, `false` otherwise.
     */
    suspend fun addAccountsWitDetails(accounts: List<DBAccount>): Boolean

    /**
     * Deletes an account and all its related details from the database.
     *
     * @param accountId The ID of the account to delete.
     * @return `true` if the account was found and deleted, `false` otherwise.
     */
    suspend fun deleteAccountWithDetails(accountId: Long): Boolean

    /**
     * Updates an existing account's primary information.
     *
     * @param account The updated account data.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateAccount(account: DBAccount): Boolean

    /**
     * Updates the emails associated with a specific account.
     *
     * @param accountId The ID of the account.
     * @param emails The new list of emails.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateEmails(accountId: Long, emails: List<String>): Boolean

    /**
     * Updates the phone numbers associated with a specific account.
     *
     * @param accountId The ID of the account.
     * @param phoneNumbers The new list of phone numbers.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updatePhoneNumbers(accountId: Long, phoneNumbers: List<String>): Boolean

    /**
     * Updates the attendance records associated with a specific account.
     *
     * @param accountId The ID of the account.
     * @param attendances The new list of attendance records.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateAttendances(accountId: Long, attendances: List<DBAttendance>): Boolean

    /**
     * Adds a single attendance record.
     *
     * @param attendance The [DBAttendance] record to be added.
     * @return `true` if added successfully, `false` otherwise.
     */
    suspend fun addAttendance(attendance: DBAttendance): Boolean

    /**
     * Adds a list of attendance records in a batch.
     *
     * @param attendances The list of [DBAttendance] records to be added.
     * @return `true` if all records were added successfully, `false` otherwise.
     */
    suspend fun addAttendances(attendances: List<DBAttendance>): Boolean

    /**
     * Updates the attendance status for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date for which to update the status.
     * @param status The new attendance status, restricted to valid values defined in [AttendanceStatusType].
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateAttendanceStatus(accountId: Long, date: Long, @AttendanceStatusType status: String): Boolean

    /**
     * Retrieves all attendance records for a specific account.
     *
     * @param accountId The ID of the account.
     * @return A list of [DBAttendance] records.
     */
    suspend fun getAttendancesByAccountId(accountId: Long): List<DBAttendance>

    /**
     * Observes attendance records for a specific account.
     *
     * @param accountId The ID of the account to observe.
     * @return A [Flow] emitting a list of [DBAttendance] records whenever changes occur.
     */
    fun observeAttendancesByAccountId(accountId: Long): Flow<List<DBAttendance>>

    /**
     * Retrieves the attendance record for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date.
     * @return The [DBAttendance] record if found, or `null` otherwise.
     */
    suspend fun getAttendanceByDate(accountId: Long, date: Long): DBAttendance?

    /**
     * Observes the attendance record for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date.
     * @return A [Flow] emitting the [DBAttendance] record or `null` whenever changes occur.
     */
    fun observeAttendanceByDate(accountId: Long, date: Long): Flow<DBAttendance?>

    /**
     * Retrieves attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return A list of [DBAttendance] records falling within the specified range.
     */
    suspend fun getAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): List<DBAttendance>

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
    ): Int

    /**
     * Observes attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return A [Flow] emitting a list of [DBAttendance] records within the range whenever changes occur.
     */
    fun observeAttendancesByDateRange(accountId: Long, startDate: Long, endDate: Long): Flow<List<DBAttendance>>

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
    suspend fun getAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, page: Int, pageSize: Int): List<DBAttendance>

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
    fun observeAttendancesByDateRangeChunked(accountId: Long, startDate: Long, endDate: Long, page: Int, pageSize: Int): Flow<List<DBAttendance>>

    /**
     * Returns a [PagingSource] for paginated access to attendance records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startDate The start timestamp/epoch date (inclusive).
     * @param endDate The end timestamp/epoch date (inclusive).
     * @return A [PagingSource] providing paged attendance records.
     */
    fun getAttendancesByDateRangePaged(accountId: Long, startDate: Long, endDate: Long): PagingSource<Int, DBAttendance>

    /**
     * Retrieves a chunk (page) of attendance records for a specific account.
     *
     * @param accountId The ID of the account.
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A list of [DBAttendance] records for the requested chunk.
     */
    suspend fun getAttendancesByAccountIdChunked(accountId: Long, page: Int, pageSize: Int): List<DBAttendance>

    /**
     * Returns a [PagingSource] for paginated access to attendance records for an account.
     *
     * @param accountId The ID of the account.
     * @return A [PagingSource] providing paged attendance records.
     */
    fun getAttendancesPaged(accountId: Long): PagingSource<Int, DBAttendance>

    /**
     * Retrieves a single account with its details by ID.
     *
     * @param accountId The ID of the account to retrieve.
     * @return The [DBAccount] if found, or `null` otherwise.
     */
    suspend fun getAccountWithDetailsById(accountId: Long): DBAccount?

    /**
     * Retrieves an account by its server ID.
     *
     * @param serverId The server ID of the account.
     * @return The [DBAccount] if found, or `null` otherwise.
     */
    suspend fun getAccountByServerId(serverId: String): DBAccount?

    /**
     * Retrieves the current user account.
     *
     * @return The [DBAccount] marked as user, or `null` if not found.
     */
    suspend fun getUserAccount(): DBAccount?

    /**
     * Retrieves an attendance record by its server ID.
     *
     * @param serverId The server ID of the attendance record.
     * @return The [DBAttendance] if found, or `null` otherwise.
     */
    suspend fun getAttendanceByServerId(serverId: String): DBAttendance?

    /**
     * Observes changes to a specific account and its details.
     *
     * @param accountId The ID of the account to observe.
     * @return A [Flow] emitting the [DBAccount] whenever it changes.
     */
    fun observeAccountWithDetailsById(accountId: Long): Flow<DBAccount?>

    /**
     * Retrieves a chunk (page) of accounts from the database.
     *
     * @param page The page number (starting from 0).
     * @param pageSize The number of accounts per page.
     * @return A list of [DBAccount]s for the requested chunk.
     */
    suspend fun getAccountsByChunk(page: Int, pageSize: Int): List<DBAccount>

    /**
     * Returns a [PagingSource] for paginated access to all accounts with details.
     */
    fun getAccountsPaged(): PagingSource<Int, DBAccount>
}
