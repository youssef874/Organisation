package com.example.orgcore.internal.commun.processor

import android.content.Context
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.processor.AttendanceUpdatePayloadProcessor
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.DBAccount
import com.example.orgdatabase.public.model.OfflineRequestType
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PayloadProcessorTest {

    private lateinit var context: Context

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    private lateinit var updateStateProcessor: AttendanceUpdatePayloadProcessor
    private lateinit var appProcessor: AppPayloadProcessor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication().applicationContext
        updateStateProcessor = AttendanceUpdatePayloadProcessor(accountDataApi)
        appProcessor = AppPayloadProcessor(updateStateProcessor)
    }

    @Test
    fun `AttendanceUpdatePayloadProcessor decodes payload correctly`() = runTest {
        val mockAccount = DBAccount(
            id = 1L,
            name = "Test User",
            isUser = true,
            accountType = 2,
            serverId = "acc-456"
        )
        `when`(accountDataApi.getAccountByServerId("acc-456")).thenReturn(mockAccount)

        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("id", "att-1")
            put("accountId", "acc-456")
            put("checkIn", 1000L)
            put("checkOut", 2000L)
            put("date", 1000L)
            put("attendanceStatus", AttendanceStatus.PRESENT)
        }

        val constraint = updateStateProcessor.decode(json)
        assertNotNull(constraint)
        assertEquals(AttendanceBusinessType.ATTENDANCE_UPDATE, constraint.businessType)
        assertEquals("acc-456", constraint.serverAccountId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AttendanceUpdatePayloadProcessor throws exception on missing serverAccountId`() = runTest {
        val json = JSONObject().apply {
            put("accountId", "")
            put("date", 1000L)
        }
        updateStateProcessor.decode(json)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AttendanceUpdatePayloadProcessor throws exception when account not found`() = runTest {
        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("id", "att-1")
            put("accountId", "acc-456")
            put("checkIn", 1000L)
            put("checkOut", 2000L)
            put("date", 1000L)
            put("attendanceStatus", AttendanceStatus.PRESENT)
        }
        `when`(accountDataApi.getAccountByServerId("acc-456")).thenReturn(null)

        updateStateProcessor.decode(json)
    }

    @Test
    fun `AppPayloadProcessor routes attendance request correctly`() = runTest {
        val mockAccount = DBAccount(
            id = 1L,
            name = "Test User",
            isUser = true,
            accountType = 2,
            serverId = "acc-456"
        )
        `when`(accountDataApi.getAccountByServerId("acc-456")).thenReturn(mockAccount)

        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("accountId", "acc-456")
            put("date", 1000L)
            put("requestType", OfflineRequestType.ATTENDANCE)
            put("businessType", AttendanceBusinessType.ATTENDANCE_UPDATE)
            put("attendanceStatus", AttendanceStatus.PRESENT)
        }

        val constraint = appProcessor.decode(json)
        assertNotNull(constraint)
        assertEquals(AttendanceBusinessType.ATTENDANCE_UPDATE, constraint.businessType)
    }

    @Test(expected = IllegalStateException::class)
    fun `Integration test throws exception on unknown request type`() = runTest {
        val json = JSONObject().apply {
            put("requestType", "unknown_type")
        }

        appProcessor.decode(json)
    }
}
