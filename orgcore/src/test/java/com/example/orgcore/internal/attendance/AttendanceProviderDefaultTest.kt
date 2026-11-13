package com.example.orgcore.internal.attendance

import android.content.Context
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.account.Authentication.provider.FakeAuthenticationProvider
import com.example.orgcore.internal.commun.event.OrgEventNotifier
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.api.OfflineRequestDataApi
import com.example.orgdatabase.public.model.DBAccount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AttendanceProviderDefaultTest {

    private lateinit var context: Context

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    @Mock
    private lateinit var networkMonitor: NetworkMonitor

    @Mock
    private lateinit var attendanceEventNotifier: OrgEventNotifier

    @Mock
    private lateinit var offlineRequestDataApi: OfflineRequestDataApi

    private lateinit var fakeAuthProvider: FakeAuthenticationProvider
    private lateinit var attendanceProvider: AttendanceProviderDefault

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication().applicationContext
        fakeAuthProvider = FakeAuthenticationProvider()
        attendanceProvider = AttendanceProviderDefault(
            context = context,
            accountDataApi = accountDataApi,
            authenticationProvider = fakeAuthProvider,
            networkMonitor = networkMonitor,
            attendanceEventNotifier = attendanceEventNotifier,
            offlineRequestDataApi = offlineRequestDataApi
        )
    }

    @Test
    fun `getAttendanceRapportByAccountId delegates to accountDataApi getAttendancesByAccountId`() = runTest {
        val accountId = 10L
        `when`(accountDataApi.getAttendancesByAccountId(accountId)).thenReturn(emptyList())

        attendanceProvider.getAttendanceRapportByAccountId(accountId)

        verify(accountDataApi).getAttendancesByAccountId(accountId)
    }

    @Test
    fun `observeAttendanceRapportByAccountId delegates to accountDataApi observeAttendancesByAccountId`() = runTest {
        val accountId = 10L
        `when`(accountDataApi.observeAttendancesByAccountId(accountId)).thenReturn(flowOf(emptyList()))

        attendanceProvider.observeAttendanceRapportByAccountId(accountId).first()

        verify(accountDataApi).observeAttendancesByAccountId(accountId)
    }

    @Test
    fun `getAttendanceRapportByRange delegates to accountDataApi getAttendancesByDateRange`() = runTest {
        val accountId = 10L
        val start = 1000L
        val end = 2000L
        `when`(accountDataApi.getAttendancesByDateRange(accountId, start, end)).thenReturn(emptyList())

        attendanceProvider.getAttendanceRapportByRange(accountId, start, end)

        verify(accountDataApi).getAttendancesByDateRange(accountId, start, end)
    }

    @Test
    fun `getAttendanceRapportByRangeChunked delegates to accountDataApi getAttendancesByDateRangeChunked`() = runTest {
        val accountId = 10L
        val start = 1000L
        val end = 2000L
        val page = 0
        val size = 10
        `when`(accountDataApi.getAttendancesByDateRangeChunked(accountId, start, end, page, size)).thenReturn(emptyList())

        attendanceProvider.getAttendanceRapportByRangeChunked(accountId, start, end, page, size)

        verify(accountDataApi).getAttendancesByDateRangeChunked(accountId, start, end, page, size)
    }

    @Test
    fun `observeAttendanceRapportByRangeChunked delegates to accountDataApi observeAttendancesByDateRangeChunked`() = runTest {
        val accountId = 10L
        val start = 1000L
        val end = 2000L
        val page = 0
        val size = 10
        `when`(accountDataApi.observeAttendancesByDateRangeChunked(accountId, start, end, page, size)).thenReturn(flowOf(emptyList()))

        attendanceProvider.observeAttendanceRapportByRangeChunked(accountId, start, end, page, size).first()

        verify(accountDataApi).observeAttendancesByDateRangeChunked(accountId, start, end, page, size)
    }

    @Test
    fun `getAttendanceRapportForDay delegates to accountDataApi getAttendanceByDate`() = runTest {
        val accountId = 10L
        val date = 1500L
        `when`(accountDataApi.getAttendanceByDate(accountId, date)).thenReturn(null)

        attendanceProvider.getAttendanceRapportForDay(accountId, date)

        verify(accountDataApi).getAttendanceByDate(accountId, date)
    }

    @Test
    fun `getAttendanceStatusCountForRange delegates to accountDataApi getAttendanceCountByStatusAndDateRange`() = runTest {
        val accountId = 10L
        val start = 1000L
        val end = 2000L
        val status = AttendanceStatus.PRESENT
        `when`(accountDataApi.getAttendanceCountByStatusAndDateRange(accountId, status, start, end)).thenReturn(0)

        attendanceProvider.getAttendanceStatusCountForRange(accountId, start, end, status)

        verify(accountDataApi).getAttendanceCountByStatusAndDateRange(accountId, status, start, end)
    }

    @Test
    fun `getUserAttendanceRapport resolves serverId and accountId from providers and delegates`() = runTest {
        val serverId = "server_user_1"
        val localAccountId = 99L
        val mockAccount = DBAccount(
            id = localAccountId,
            name = "Test User",
            isUser = true,
            accountType = 2,
            serverId = serverId
        )

        fakeAuthProvider.authenticatedUserServerIdDataStoreItemProvider.updateData(serverId)
        `when`(accountDataApi.getAccountByServerId(serverId)).thenReturn(mockAccount)
        `when`(accountDataApi.getAttendancesByAccountId(localAccountId)).thenReturn(emptyList())

        val result = attendanceProvider.getUserAttendanceRapport()

        assertEquals(emptyList<Any>(), result)
        verify(accountDataApi).getAccountByServerId(serverId)
        verify(accountDataApi).getAttendancesByAccountId(localAccountId)
    }

    @Test
    fun `getUserAttendanceRapport returns empty list when no user is authenticated`() = runTest {
        val result = attendanceProvider.getUserAttendanceRapport()

        assertEquals(emptyList<Any>(), result)
    }
}
