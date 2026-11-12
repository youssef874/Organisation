package com.example.orgcore.internal.attendance

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.api.attendance.model.OrgAttendance
import com.example.orgcore.internal.ServicesProvider
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.attendance.mapper.toOrgAttendance
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgdatabase.public.api.AccountDataApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

internal class AttendanceProviderDefault(
    private val context: Context,
    private val accountDataApi: AccountDataApi = AccountDataApi(context),
    private val authenticationProvider: AuthenticationProvider = ServicesProvider.getAuthenticationProvider(context)
) : AttendanceProvider {

    companion object {
        private const val TAG = "ATTENDANCE"
        private const val CLASS_NAME = "AttendanceProviderDefault"
    }

    override suspend fun getAttendanceRapportByAccountId(
        accountId: Long
    ): List<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getAttendanceRapportByAccountId|accountId: $accountId")
        return accountDataApi.getAttendancesByAccountId(accountId).map { it.toOrgAttendance() }
    }

    override fun observeAttendanceRapportByAccountId(
        accountId: Long
    ): Flow<List<OrgAttendance>> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeAttendanceRapportByAccountId|accountId: $accountId")
        return accountDataApi.observeAttendancesByAccountId(accountId).map { list ->
            list.map { it.toOrgAttendance() }
        }
    }

    override suspend fun getAttendanceRapportByRange(
        accountId: Long,
        startRange: Long,
        endRange: Long
    ): List<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getAttendanceRapportByRange|accountId: $accountId, startRange: $startRange, endRange: $endRange")
        return accountDataApi.getAttendancesByDateRange(accountId, startRange, endRange).map { it.toOrgAttendance() }
    }

    override suspend fun getAttendanceRapportByRangeChunked(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): List<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getAttendanceRapportByRangeChunked|accountId: $accountId, page: $page, pageSize: $pageSize")
        return accountDataApi.getAttendancesByDateRangeChunked(accountId, startRange, endRange, page, pageSize).map { it.toOrgAttendance() }
    }

    override fun observeAttendanceRapportByRangeChunked(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): Flow<List<OrgAttendance>> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeAttendanceRapportByRangeChunked|accountId: $accountId, page: $page, pageSize: $pageSize")
        return accountDataApi.observeAttendancesByDateRangeChunked(accountId, startRange, endRange, page, pageSize).map { list ->
            list.map { it.toOrgAttendance() }
        }
    }

    override fun getAttendanceRapportByRangePaged(
        accountId: Long,
        startRange: Long,
        endRange: Long
    ): PagingSource<Int, OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getAttendanceRapportByRangePaged|accountId: $accountId")
        return MappedPagingSource(
            accountDataApi.getAttendancesByDateRangePaged(accountId, startRange, endRange)
        ) { it.toOrgAttendance() }
    }

    override fun observeAttendanceRapportByRange(
        accountId: Long,
        startRange: Long,
        endRange: Long
    ): Flow<List<OrgAttendance>> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeAttendanceRapportByRange|accountId: $accountId")
        return accountDataApi.observeAttendancesByDateRange(accountId, startRange, endRange).map { list ->
            list.map { it.toOrgAttendance() }
        }
    }

    override suspend fun getAttendanceRapportForDay(
        accountId: Long,
        date: Long
    ): OrgAttendance? {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getAttendanceRapportForDay|accountId: $accountId, date: $date")
        return accountDataApi.getAttendanceByDate(accountId, date)?.toOrgAttendance()
    }

    override fun observeAttendanceRapportForDay(
        accountId: Long,
        date: Long
    ): Flow<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeAttendanceRapportForDay|accountId: $accountId, date: $date")
        return accountDataApi.observeAttendanceByDate(accountId, date).mapNotNull { it?.toOrgAttendance() }
    }

    override suspend fun getAttendanceStatusCountForRange(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Int {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getAttendanceStatusCountForRange|accountId: $accountId, status: $attendanceStatus")
        return accountDataApi.getAttendanceCountByStatusAndDateRange(accountId, attendanceStatus, startRange, endRange)
    }

    override fun observeAttendanceStatusCountForRange(
        accountId: Long,
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Flow<Int> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeAttendanceStatusCountForRange|accountId: $accountId, status: $attendanceStatus")
        return accountDataApi.observeAttendancesByDateRange(accountId, startRange, endRange).map { list ->
            list.count { it.attendanceStatus == attendanceStatus }
        }
    }

    private suspend fun getAuthenticatedUserAccountId(): Long? {
        val serverId = authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData("")
        if (serverId.isBlank()) {
            LoggerHelper.logger.w(TAG, "$CLASS_NAME|getAuthenticatedUserAccountId|no authenticated user server id found")
            return null
        }
        val account = accountDataApi.getAccountByServerId(serverId)
        if (account == null) {
            LoggerHelper.logger.w(TAG, "$CLASS_NAME|getAuthenticatedUserAccountId|no account found for server id: $serverId")
            return null
        }
        return account.id
    }

    private fun <T> flowWithUserAccountId(
        defaultIfNoUser: T,
        block: (Long) -> Flow<T>
    ): Flow<T> = flow {
        val accountId = getAuthenticatedUserAccountId()
        if (accountId == null) {
            emit(defaultIfNoUser)
        } else {
            emitAll(block(accountId))
        }
    }

    override suspend fun getUserAttendanceRapport(): List<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getUserAttendanceRapport")
        val accountId = getAuthenticatedUserAccountId() ?: return emptyList()
        return getAttendanceRapportByAccountId(accountId)
    }

    override fun observeUserAttendanceRapport(): Flow<List<OrgAttendance>> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeUserAttendanceRapport")
        return flowWithUserAccountId(emptyList()) { accountId ->
            observeAttendanceRapportByAccountId(accountId)
        }
    }

    override suspend fun getUserAttendanceRapportByRange(
        startRange: Long,
        endRange: Long
    ): List<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getUserAttendanceRapportByRange|startRange: $startRange, endRange: $endRange")
        val accountId = getAuthenticatedUserAccountId() ?: return emptyList()
        return getAttendanceRapportByRange(accountId, startRange, endRange)
    }

    override suspend fun getUserAttendanceRapportByRangeChunked(
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): List<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getUserAttendanceRapportByRangeChunked|page: $page, pageSize: $pageSize")
        val accountId = getAuthenticatedUserAccountId() ?: return emptyList()
        return getAttendanceRapportByRangeChunked(accountId, startRange, endRange, page, pageSize)
    }

    override fun observeUserAttendanceRapportByRangeChunked(
        startRange: Long,
        endRange: Long,
        page: Int,
        pageSize: Int
    ): Flow<List<OrgAttendance>> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeUserAttendanceRapportByRangeChunked|page: $page, pageSize: $pageSize")
        return flowWithUserAccountId(emptyList()) { accountId ->
            observeAttendanceRapportByRangeChunked(accountId, startRange, endRange, page, pageSize)
        }
    }

    override fun getUserAttendanceRapportByRangePaged(
        startRange: Long,
        endRange: Long
    ): PagingSource<Int, OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getUserAttendanceRapportByRangePaged|startRange: $startRange, endRange: $endRange")
        return UserAttendancePagingSource(
            getAccountId = { getAuthenticatedUserAccountId() },
            createDelegate = { accountId ->
                getAttendanceRapportByRangePaged(accountId, startRange, endRange)
            }
        )
    }

    override fun observeUserAttendanceRapportByRange(
        startRange: Long,
        endRange: Long
    ): Flow<List<OrgAttendance>> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeUserAttendanceRapportByRange|startRange: $startRange, endRange: $endRange")
        return flowWithUserAccountId(emptyList()) { accountId ->
            observeAttendanceRapportByRange(accountId, startRange, endRange)
        }
    }

    override suspend fun getUserAttendanceRapportForDay(
        date: Long
    ): OrgAttendance? {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getUserAttendanceRapportForDay|date: $date")
        val accountId = getAuthenticatedUserAccountId() ?: return null
        return getAttendanceRapportForDay(accountId, date)
    }

    override fun observeUserAttendanceRapportForDay(
        date: Long
    ): Flow<OrgAttendance> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeUserAttendanceRapportForDay|date: $date")
        return flow {
            val accountId = getAuthenticatedUserAccountId()
            if (accountId != null) {
                emitAll(observeAttendanceRapportForDay(accountId, date))
            }
        }
    }

    override suspend fun getUserAttendanceStatusCountForRange(
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Int {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|getUserAttendanceStatusCountForRange|status: $attendanceStatus")
        val accountId = getAuthenticatedUserAccountId() ?: return 0
        return getAttendanceStatusCountForRange(accountId, startRange, endRange, attendanceStatus)
    }

    override fun observeUserAttendanceStatusCountForRange(
        startRange: Long,
        endRange: Long,
        attendanceStatus: @AttendanceStatus String
    ): Flow<Int> {
        LoggerHelper.logger.i(TAG, "$CLASS_NAME|observeUserAttendanceStatusCountForRange|status: $attendanceStatus")
        return flowWithUserAccountId(0) { accountId ->
            observeAttendanceStatusCountForRange(accountId, startRange, endRange, attendanceStatus)
        }
    }
}

private class UserAttendancePagingSource(
    private val getAccountId: suspend () -> Long?,
    private val createDelegate: (Long) -> PagingSource<Int, OrgAttendance>
) : PagingSource<Int, OrgAttendance>() {
    private var delegate: PagingSource<Int, OrgAttendance>? = null

    override fun getRefreshKey(state: PagingState<Int, OrgAttendance>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrgAttendance> {
        val accountId = getAccountId() ?: return LoadResult.Page(emptyList(), null, null)
        val activeDelegate = delegate ?: createDelegate(accountId).also {
            delegate = it
            it.registerInvalidatedCallback { invalidate() }
        }
        return activeDelegate.load(params)
    }

    override val jumpingSupported: Boolean get() = delegate?.jumpingSupported ?: false
    override val keyReuseSupported: Boolean get() = delegate?.keyReuseSupported ?: false
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
