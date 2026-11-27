package com.example.orgcore.internal.commun.processor

import android.content.Context
import com.example.orgcore.internal.attendance.model.AttendanceBusinessType
import com.example.orgcore.internal.attendance.processor.AttendancePayloadProcessor
import com.example.orgcore.internal.attendance.processor.CancelAttendanceRequestPayloadProcessor
import com.example.orgdatabase.public.model.OfflineRequestType
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PayloadProcessorTest {

    private lateinit var context: Context
    private lateinit var cancelProcessor: CancelAttendanceRequestPayloadProcessor
    private lateinit var attendanceProcessor: AttendancePayloadProcessor
    private lateinit var appProcessor: AppPayloadProcessor

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication().applicationContext
        cancelProcessor = CancelAttendanceRequestPayloadProcessor()
        attendanceProcessor = AttendancePayloadProcessor { cancelProcessor }
        appProcessor = AppPayloadProcessor { attendanceProcessor }
    }

    @Test
    fun `CancelAttendanceRequestPayloadProcessor decodes payload correctly`() = runTest {
        val json = JSONObject().apply {
            put("accountId", "acc-456")
            put("date", 1000L)
        }

        val constraint = cancelProcessor.decode(json)
        assertNotNull(constraint)
        assertEquals(AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST, constraint.businessType)
        assertEquals("acc-456", constraint.accountId)
        assertEquals(1000L, constraint.date)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `CancelAttendanceRequestPayloadProcessor throws exception on missing accountId`() = runTest {
        val json = JSONObject().apply {
            put("accountId", "")
            put("date", 1000L)
        }
        cancelProcessor.decode(json)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `CancelAttendanceRequestPayloadProcessor throws exception on invalid date`() = runTest {
        val json = JSONObject().apply {
            put("accountId", "acc-456")
            put("date", 0L)
        }
        cancelProcessor.decode(json)
    }

    @Test
    fun `AppPayloadProcessor routes cancel attendance request correctly`() = runTest {
        val json = JSONObject().apply {
            put("requestType", OfflineRequestType.ATTENDANCE)
            put("businessType", AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST)
            put("accountId", "acc-456")
            put("date", 1000L)
        }

        val constraint = appProcessor.decode(json)
        assertNotNull(constraint)
        assertEquals(AttendanceBusinessType.CANCEL_ATTENDANCE_REQUEST, constraint.businessType)
    }

    @Test(expected = IllegalStateException::class)
    fun `Integration test throws exception on unknown request type`() = runTest {
        val json = JSONObject().apply {
            put("requestType", "unknown_type")
        }

        appProcessor.decode(json)
    }
}
