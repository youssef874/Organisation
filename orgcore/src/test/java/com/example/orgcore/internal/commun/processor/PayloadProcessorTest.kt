package com.example.orgcore.internal.commun.processor

import android.content.Context
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.cache.AccountAttendanceStateUpdateCache
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.processor.AttendancePayloadProcessor
import com.example.orgcore.internal.attendance.processor.AttendanceUpdateStatePayloadProcessor
import com.example.orgcore.internal.attendance.request.FakerRequestAccountAttendanceStatusUpdate
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgdatabase.public.api.AccountDataApi
import com.example.orgdatabase.public.model.OfflineRequestType
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PayloadProcessorTest {

    private lateinit var context: Context

    @Mock
    private lateinit var accountDataApi: AccountDataApi

    private lateinit var updateStateProcessor: AttendanceUpdateStatePayloadProcessor
    private lateinit var attendanceProcessor: AttendancePayloadProcessor
    private lateinit var appProcessor: AppPayloadProcessor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication().applicationContext
        updateStateProcessor = AttendanceUpdateStatePayloadProcessor(accountDataApi)
        attendanceProcessor = AttendancePayloadProcessor(updateStateProcessor)
        appProcessor = AppPayloadProcessor(attendanceProcessor)
    }

    @Test
    fun `AttendanceUpdateStatePayloadProcessor decodes payload correctly`() = runTest {
        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("accountId", 456L)
            put("date", 1000L)
            put("status", AttendanceStatus.PRESENT)
        }

        updateStateProcessor.decode(json)

        val request = updateStateProcessor.getServerRequest()
        assertNotNull(request)
        assertTrue(request is FakerRequestAccountAttendanceStatusUpdate)

        val cache = updateStateProcessor.getCache()
        assertNotNull(cache)
        assertTrue(cache is AccountAttendanceStateUpdateCache)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AttendanceUpdateStatePayloadProcessor throws exception on zero or missing accountId`() = runTest {
        val json = JSONObject().apply {
            put("accountId", 0L)
            put("date", 1000L)
        }
        updateStateProcessor.decode(json)
    }

    @Test
    fun `AttendancePayloadProcessor routes cancel attendance request correctly`() = runTest {
        var decodeInvoked = false
        var getServerRequestInvoked = false
        var getCacheInvoked = false

        @Suppress("UNCHECKED_CAST")
        val fakeUpdateStateProcessor = object : PayloadProcessor<Any> {
            override suspend fun decode(json: JSONObject) {
                decodeInvoked = true
            }
            override fun getServerRequest(): ServerRequest<Any> {
                getServerRequestInvoked = true
                return mock(ServerRequest::class.java) as ServerRequest<Any>
            }
            override fun getCache(): OfflineCache<Any>? {
                getCacheInvoked = true
                return mock(OfflineCache::class.java) as OfflineCache<Any>
            }
        }

        val processorWithFake = AttendancePayloadProcessor(fakeUpdateStateProcessor)

        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("accountId", 456L)
            put("date", 1000L)
            put("businessType", AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST)
            put("status", AttendanceStatus.NONE)
        }

        processorWithFake.decode(json)
        assertTrue(decodeInvoked)

        val request = processorWithFake.getServerRequest()
        assertNotNull(request)
        assertTrue(getServerRequestInvoked)

        val cache = processorWithFake.getCache()
        assertNotNull(cache)
        assertTrue(getCacheInvoked)
    }

    @Test
    fun `AppPayloadProcessor routes attendance request correctly`() = runTest {
        var decodeInvoked = false
        var getServerRequestInvoked = false
        var getCacheInvoked = false

        @Suppress("UNCHECKED_CAST")
        val fakeAttendanceProcessor = object : PayloadProcessor<Any> {
            override suspend fun decode(json: JSONObject) {
                decodeInvoked = true
            }
            override fun getServerRequest(): ServerRequest<Any> {
                getServerRequestInvoked = true
                return mock(ServerRequest::class.java) as ServerRequest<Any>
            }
            override fun getCache(): OfflineCache<Any>? {
                getCacheInvoked = true
                return mock(OfflineCache::class.java) as OfflineCache<Any>
            }
        }

        val appProcessorWithFake = AppPayloadProcessor(fakeAttendanceProcessor)

        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("accountId", 456L)
            put("date", 1000L)
            put("requestType", OfflineRequestType.ATTENDANCE)
            put("businessType", AttendanceBusinessType.ATTENDANCE_STATUS_UPDATE)
            put("status", AttendanceStatus.PRESENT)
        }

        appProcessorWithFake.decode(json)
        assertTrue(decodeInvoked)

        val request = appProcessorWithFake.getServerRequest()
        assertNotNull(request)
        assertTrue(getServerRequestInvoked)

        val cache = appProcessorWithFake.getCache()
        assertNotNull(cache)
        assertTrue(getCacheInvoked)
    }

    @Test
    fun `Integration test full B-tree routing chain from App to Leaf`() = runTest {
        val rawPayload = """
            {
                "requestUuid": "integration-uuid-999",
                "accountId": 789,
                "date": 50000,
                "requestType": "attendance",
                "businessType": "cancel_attendance_request",
                "status": "none"
            }
        """.trimIndent()

        // Decode using root AppProcessor with parsed JSONObject
        appProcessor.decode(JSONObject(rawPayload))

        val request = appProcessor.getServerRequest()
        val cache = appProcessor.getCache()

        assertNotNull(request)
        assertNotNull(cache)
    }

    @Test(expected = IllegalStateException::class)
    fun `Integration test throws exception on unknown request type`() = runTest {
        val json = JSONObject().apply {
            put("requestType", "unknown_type")
        }

        appProcessor.decode(json)
        appProcessor.getServerRequest()
    }
}
