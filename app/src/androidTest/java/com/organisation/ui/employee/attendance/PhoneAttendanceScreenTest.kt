package com.organisation.ui.employee.attendance

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.PhoneAttendanceScreen
import com.organisation.ui.attendance.model.AttendanceTestTags
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class PhoneAttendanceScreenTest {

    @get:Rule
    val rule = createComposeRule()
    private val calendarWidget get() = rule.onNodeWithTag(AttendanceTestTags.CALENDAR_WIDGET)
    private val attendanceHeader get() = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_HEADER)
    private val previousMonthButton get() = rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON)
    private val nextMonthButton get() = rule.onNodeWithTag(AttendanceTestTags.NEXT_MONTH_BUTTON)
    private val requestOffButton get() = rule.onNodeWithTag(AttendanceTestTags.REQUEST_OFF_BUTTON)
    private val cancelRequestButton get() = rule.onNodeWithTag(AttendanceTestTags.CANCEL_REQUEST_BUTTON)
    private val pendingRequestsFab get() = rule.onNodeWithTag(AttendanceTestTags.PENDING_REQUESTS_FAB)
    private val calendarLoadingIndicator get() = rule.onNodeWithTag(AttendanceTestTags.CALENDAR_LOADING_INDICATOR)
    private val actionButtonLoadingIndicator get() = rule.onNodeWithTag(AttendanceTestTags.ACTION_BUTTONS_LOADING_INDICATOR)
    private val attendanceRequestItemLoadingIndicator get() = rule.onNodeWithTag(AttendanceTestTags.REQUEST_ITEM_LOADING_INDICATOR)
    private val actionButton get() = rule.onNodeWithTag(AttendanceTestTags.ACTION_BUTTONS)
    private val requestItem get() = rule.onNodeWithTag(AttendanceTestTags.REQUEST_ITEM)
    private val attendanceStatusBadge get() = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_STATUS_BADGE)

    @Test
    fun phoneAttendanceScreen_whenLoaded_displaysCorrectComponents() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }

        attendanceHeader.assertIsDisplayed()
        calendarWidget.assertIsDisplayed()
        actionButton.assertIsDisplayed()
        requestItem.assertIsDisplayed()
        pendingRequestsFab.assertIsDisplayed()
        attendanceStatusBadge.assertIsDisplayed()
    }

    @Test
    fun phoneAttendanceScreen_whenLoading_showsLoadingState() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(emptyMap<LocalDate, AttendanceRecord>())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(true)
        val isCalendarLoading = MutableStateFlow(true)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }

        calendarLoadingIndicator.assertIsDisplayed()
        actionButtonLoadingIndicator.assertIsDisplayed()
        attendanceRequestItemLoadingIndicator.assertIsDisplayed()
    }

    @Test
    fun phoneAttendanceScreen_monthNavigation_triggersCallbacks() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var previousMonthClicked = false
        var nextMonthClicked = false

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onPreviousMonth = {
                    previousMonthClicked = true
                    month.update {
                        month.value.minusMonths(1)
                    }
                },
                onNextMonth = {
                    nextMonthClicked = true
                    month.update {
                        month.value.plusMonths(1)
                    }
                },
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }
        val monthText = rule.onNodeWithTag(AttendanceTestTags.CALENDAR_MONTH_TEXT)
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

        previousMonthButton.performClick()
        assertTrue(previousMonthClicked)
        assertTrue(month.value.monthValue == LocalDate.now().minusMonths(1).monthValue)
        monthText.assertTextEquals(formatter.format(month.value))

        nextMonthButton.performClick()
        assertTrue(nextMonthClicked)
        assertTrue(month.value.monthValue == LocalDate.now().monthValue)
        monthText.assertTextEquals(formatter.format(month.value))
    }

    @Test
    fun phoneAttendanceScreen_requestOffButton_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2]) // Present
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var requestOffClicked = false

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = { requestOffClicked = true },
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }

        requestOffButton.assertIsDisplayed()
        requestOffButton.performClick()
        assertTrue(requestOffClicked)
    }

    @Test
    fun phoneAttendanceScreen_cancelRequestButton_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[6]) // Pending
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var cancelRequestClicked = false

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = { cancelRequestClicked = true },
                navigateToPendingRequestScreen = {}
            )
        }

        cancelRequestButton.assertIsDisplayed()
        cancelRequestButton.performClick()
        assertTrue(cancelRequestClicked)
    }

    @Test
    fun phoneAttendanceScreen_pendingRequestsNavigation_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var navigatePendingClicked = false

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = { navigatePendingClicked = true }
            )
        }

        pendingRequestsFab.performClick()
        assertTrue(navigatePendingClicked)
    }

    @Test
    fun phoneAttendanceScreen_daySelection_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.values.toList()[2])
        val daysMap = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())

        var selectedRecord: AttendanceRecord? = null

        rule.setContent {
            PhoneAttendanceScreen(
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = { selectedRecord = it },
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }

        rule.onNode(hasTestTag(AttendanceTestTags.CALENDAR_DAY_ITEM) and hasText(selectedDay.value.dayOfMonth.toString()))
            .performClick()
        assertTrue(selectedRecord != null)
        assertTrue(selectedRecord?.dayOfMonth == 3)
    }
}
