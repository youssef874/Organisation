package com.example.orgcore.internal.attendance

import androidx.paging.PagingSource
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.api.attendance.model.OrgAttendance
import kotlinx.coroutines.flow.Flow

/**
 * Internal provider interface for accessing and observing attendance reports ([OrgAttendance]).
 */
internal interface AttendanceProvider {

    /**
     * Retrieves all attendance report records for a specific account.
     *
     * @param accountId The ID of the account.
     * @return A list of all [OrgAttendance] records for the account.
     */
    suspend fun getAttendanceRapportByAccountId(
        accountId: Long
    ): List<OrgAttendance>

    /**
     * Observes all attendance report records for a specific account.
     *
     * @param accountId The ID of the account.
     * @return A [Flow] emitting a list of all [OrgAttendance] records for the account whenever changes occur.
     */
    fun observeAttendanceRapportByAccountId(
        accountId: Long
    ): Flow<List<OrgAttendance>>

    /**
     * Retrieves attendance report records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @return A list of [OrgAttendance] records within the range.
     */
    suspend fun getAttendanceRapportByRange(
        accountId: Long,
        startRange: Long,
        endRange: Long
    ): List<OrgAttendance>

    /**
     * Retrieves a chunk (page) of attendance report records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A list of [OrgAttendance] records for the requested chunk.
     */
    suspend fun getAttendanceRapportByRangeChunked(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): List<OrgAttendance>

    /**
     * Observes a chunk (page) of attendance report records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A [Flow] emitting a list of [OrgAttendance] records for the requested chunk whenever changes occur.
     */
    fun observeAttendanceRapportByRangeChunked(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): Flow<List<OrgAttendance>>

    /**
     * Returns a [PagingSource] for paginated access to attendance report records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @return A [PagingSource] providing paged [OrgAttendance] records.
     */
    fun getAttendanceRapportByRangePaged(
        accountId: Long,
        startRange: Long,
        endRange: Long
    ): PagingSource<Int, OrgAttendance>

    /**
     * Observes attendance report records for a specific account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @return A [Flow] emitting a list of [OrgAttendance] records within the range whenever changes occur.
     */
    fun observeAttendanceRapportByRange(
        accountId: Long,
        startRange: Long,
        endRange: Long
    ): Flow<List<OrgAttendance>>

    /**
     * Retrieves the attendance report record for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date.
     * @return The [OrgAttendance] record if found, or `null` otherwise.
     */
    suspend fun getAttendanceRapportForDay(
        accountId: Long,
        date: Long
    ): OrgAttendance?

    /**
     * Observes the attendance report record for a specific account on a given date.
     *
     * @param accountId The ID of the account.
     * @param date The timestamp/epoch date.
     * @return A [Flow] emitting the [OrgAttendance] record whenever changes occur.
     */
    fun observeAttendanceRapportForDay(
        accountId: Long,
        date: Long
    ): Flow<OrgAttendance>

    /**
     * Retrieves the count of attendance records with a specific status for an account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param attendanceStatus The attendance status to count.
     * @return The count of matching attendance records.
     */
    suspend fun getAttendanceStatusCountForRange(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Int

    /**
     * Observes the count of attendance records with a specific status for an account within a date range.
     *
     * @param accountId The ID of the account.
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param attendanceStatus The attendance status to count.
     * @return A [Flow] emitting the count whenever changes occur.
     */
    fun observeAttendanceStatusCountForRange(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Flow<Int>

    /**
     * Retrieves all attendance report records for the authenticated user.
     *
     * @return A list of all [OrgAttendance] records for the authenticated user.
     */
    suspend fun getUserAttendanceRapport(): List<OrgAttendance>

    /**
     * Observes all attendance report records for the authenticated user.
     *
     * @return A [Flow] emitting a list of all [OrgAttendance] records for the authenticated user.
     */
    fun observeUserAttendanceRapport(): Flow<List<OrgAttendance>>

    /**
     * Retrieves attendance report records for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @return A list of [OrgAttendance] records within the range.
     */
    suspend fun getUserAttendanceRapportByRange(
        startRange: Long,
        endRange: Long
    ): List<OrgAttendance>

    /**
     * Retrieves a chunk (page) of attendance report records for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A list of [OrgAttendance] records for the requested chunk.
     */
    suspend fun getUserAttendanceRapportByRangeChunked(
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): List<OrgAttendance>

    /**
     * Observes a chunk (page) of attendance report records for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param page The page number (starting from 0).
     * @param pageSize The number of records per page.
     * @return A [Flow] emitting a list of [OrgAttendance] records for the requested chunk.
     */
    fun observeUserAttendanceRapportByRangeChunked(
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): Flow<List<OrgAttendance>>

    /**
     * Returns a [PagingSource] for paginated access to attendance report records for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @return A [PagingSource] providing paged [OrgAttendance] records.
     */
    fun getUserAttendanceRapportByRangePaged(
        startRange: Long,
        endRange: Long
    ): PagingSource<Int, OrgAttendance>

    /**
     * Observes attendance report records for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @return A [Flow] emitting a list of [OrgAttendance] records within the range.
     */
    fun observeUserAttendanceRapportByRange(
        startRange: Long,
        endRange: Long
    ): Flow<List<OrgAttendance>>

    /**
     * Retrieves the attendance report record for the authenticated user on a given date.
     *
     * @param date The timestamp/epoch date.
     * @return The [OrgAttendance] record if found, or `null` otherwise.
     */
    suspend fun getUserAttendanceRapportForDay(
        date: Long
    ): OrgAttendance?

    /**
     * Observes the attendance report record for the authenticated user on a given date.
     *
     * @param date The timestamp/epoch date.
     * @return A [Flow] emitting the [OrgAttendance] record whenever changes occur.
     */
    fun observeUserAttendanceRapportForDay(
        date: Long
    ): Flow<OrgAttendance>

    /**
     * Retrieves the count of attendance records with a specific status for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param attendanceStatus The attendance status to count.
     * @return The count of matching attendance records.
     */
    suspend fun getUserAttendanceStatusCountForRange(
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Int

    /**
     * Observes the count of attendance records with a specific status for the authenticated user within a date range.
     *
     * @param startRange The start timestamp/epoch date (inclusive).
     * @param endRange The end timestamp/epoch date (inclusive).
     * @param attendanceStatus The attendance status to count.
     * @return A [Flow] emitting the count whenever changes occur.
     */
    fun observeUserAttendanceStatusCountForRange(
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Flow<Int>
}
