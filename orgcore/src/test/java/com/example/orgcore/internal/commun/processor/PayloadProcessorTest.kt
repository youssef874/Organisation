package com.example.orgcore.internal.commun.processor

import android.content.Context
import com.example.orgcore.api.attendance.model.AttendanceStatus
import com.example.orgcore.internal.attendance.cache.AttendanceUpdateCache
import com.example.orgcore.internal.attendance.model.Attendance
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.processor.AttendancePayloadProcessor
import com.example.orgcore.internal.attendance.processor.AttendanceUpdatePayloadProcessor
import com.example.orgcore.internal.attendance.request.FakeAttendanceUpdatesServerRequest
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

    private lateinit var updateStateProcessor: AttendanceUpdatePayloadProcessor
    private lateinit var attendanceProcessor: AttendancePayloadProcessor
    private lateinit var appProcessor: AppPayloadProcessor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication().applicationContext
        updateStateProcessor = AttendanceUpdatePayloadProcessor(accountDataApi)
        attendanceProcessor = AttendancePayloadProcessor(updateStateProcessor)
        appProcessor = AppPayloadProcessor(attendanceProcessor)
    }

    @Test
    fun `AttendanceUpdatePayloadProcessor decodes payload correctly`() = runTest {
        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("id", "att-1")
            put("accountId", "acc-456")
            put("checkIn", 1000L)
            put("checkOut", 2000L)
            put("date", 1000L)
            put("attendanceStatus", AttendanceStatus.PRESENT)
        }

        updateStateProcessor.decode(json)

        val request = updateStateProcessor.getServerRequest()
        assertNotNull(request)
        assertTrue(request is FakeAttendanceUpdatesServerRequest)

        val cache = updateStateProcessor.getCache()
        assertNotNull(cache)
        assertTrue(cache is AttendanceUpdateCache)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AttendanceUpdatePayloadProcessor throws exception on missing serverAccountId`() = runTest {
        val json = JSONObject().apply {
            put("accountId", "")
            put("date", 1000L)
        }
        updateStateProcessor.decode(json)
    }

    @Test
    fun `AttendancePayloadProcessor routes attendance update request correctly`() = runTest {
        var decodeInvoked = false
        var getServerRequestInvoked = false
        var getCacheInvoked = false

        val fakeUpdateStateProcessor = object : PayloadProcessor<Attendance> {
            override suspend fun decode(json: JSONObject) {
                decodeInvoked = true
            }
            override fun getServerRequest(): ServerRequest<Attendance> {
                getServerRequestInvoked = true
                return mock(ServerRequest::class.java) as ServerRequest<Attendance>
            }
            override fun getCache(): OfflineCache<Attendance>? {
                getCacheInvoked = true
                return mock(OfflineCache::class.java) as OfflineCache<Attendance>
            }
        }

        val processorWithFake = AttendancePayloadProcessor(fakeUpdateStateProcessor)

        val json = JSONObject().apply {
            put("requestUuid", "uuid-123")
            put("accountId", "acc-456")
            put("date", 1000L)
            put("businessType", AttendanceBusinessType.ATTENDANCE_UPDATE)
            put("attendanceStatus", AttendanceStatus.PRESENT)
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
            put("accountId", "acc-456")
            put("date", 1000L)
            put("requestType", OfflineRequestType.ATTENDANCE)
            put("businessType", AttendanceBusinessType.ATTENDANCE_UPDATE)
            put("attendanceStatus", AttendanceStatus.PRESENT)
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
                "id": "att-999",
                "accountId": "acc-789",
                "date": 50000,
                "requestType": "attendance",
                "businessType": "attendance_update",
                "attendanceStatus": "present"
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
