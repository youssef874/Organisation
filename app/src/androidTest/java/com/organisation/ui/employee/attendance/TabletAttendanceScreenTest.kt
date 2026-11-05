package com.organisation.ui.employee.attendance

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.TabletAttendanceScreenContent
import com.organisation.ui.attendance.model.AttendanceTestTags
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TabletAttendanceScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val tabletScreen get() = rule.onNodeWithTag(AttendanceTestTags.TABLET_SCREEN)
    private val calendarWidget get() = rule.onNodeWithTag(AttendanceTestTags.CALENDAR_WIDGET)
    private val attendanceHeader get() = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_HEADER)
    private val previousMonthButton get() = rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON)
    private val nextMonthButton get() = rule.onNodeWithTag(AttendanceTestTags.NEXT_MONTH_BUTTON)
    private val requestOffButton get() = rule.onNodeWithTag(AttendanceTestTags.REQUEST_OFF_BUTTON)
    private val cancelRequestButton get() = rule.onNodeWithTag(AttendanceTestTags.CANCEL_REQUEST_BUTTON)
    private val calendarLoadingIndicator get() = rule.onNodeWithTag(AttendanceTestTags.CALENDAR_LOADING_INDICATOR)

    @Test
    fun tabletAttendanceScreen_whenLoaded_displaysCorrectComponents() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {}
            )
        }

        tabletScreen.assertIsDisplayed()
        attendanceHeader.assertIsDisplayed()
        calendarWidget.assertIsDisplayed()
    }

    @Test
    fun tabletAttendanceScreen_whenLoading_showsLoadingState() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(emptyMap<LocalDate, AttendanceRecord>())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(true)
        val isCalendarLoading = MutableStateFlow(true)
        val isPendingListLoading = MutableStateFlow(true)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {}
            )
        }

        calendarLoadingIndicator.assertIsDisplayed()
    }

    @Test
    fun tabletAttendanceScreen_monthNavigation_triggersCallbacks() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var previousMonthClicked = false
        var nextMonthClicked = false

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onPreviousMonth = { previousMonthClicked = true },
                onNextMonth = { nextMonthClicked = true },
                onRequestOff = {},
                cancelRequest = {}
            )
        }

        previousMonthButton.performClick()
        assertTrue(previousMonthClicked)

        nextMonthButton.performClick()
        assertTrue(nextMonthClicked)
    }

    @Test
    fun tabletAttendanceScreen_requestOffButton_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var requestOffClicked = false

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = { requestOffClicked = true },
                cancelRequest = {}
            )
        }

        requestOffButton.performClick()
        assertTrue(requestOffClicked)
    }

    @Test
    fun tabletAttendanceScreen_cancelRequestButton_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[6]) // Pending
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var cancelRequestClicked = false

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = { cancelRequestClicked = true }
            )
        }

        cancelRequestButton.performClick()
        assertTrue(cancelRequestClicked)
    }

    @Test
    fun tabletAttendanceScreen_pendingRequestsList_isDisplayed() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[6]) // Pending
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {}
            )
        }

        rule.onNodeWithText("Pending Requests").assertIsDisplayed()
    }

    @Test
    fun tabletAttendanceScreen_daySelection_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var selectedRecord: AttendanceRecord? = null

        rule.setContent {
            TabletAttendanceScreenContent(
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = { selectedRecord = it },
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {}
            )
        }

        rule.onNode(hasTestTag(AttendanceTestTags.CALENDAR_DAY_ITEM) and hasText(selectedDay.value.dayOfMonth.toString()))
            .performClick()
        assertTrue(selectedRecord != null)
    }
}
