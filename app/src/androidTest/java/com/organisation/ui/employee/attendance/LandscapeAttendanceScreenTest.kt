package com.organisation.ui.employee.attendance

import android.content.res.Configuration
import android.view.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.LandscapeAttendanceScreenContent
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.attendance.model.DateRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class LandscapeAttendanceScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Before
    fun setUp() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (uiDevice.displayRotation == Surface.ROTATION_0 || uiDevice.displayRotation == Surface.ROTATION_180) {
            uiDevice.setOrientationLandscape()
        }
    }

    @After
    fun tearDown() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.setOrientationNatural()
    }

    private val weeklyCalendarWidget get() = rule.onNodeWithTag(AttendanceTestTags.WEEKLY_CALENDAR_WIDGET)
    private val attendanceHeader get() = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_HEADER)
    private val previousWeekButton get() = rule.onNode(hasTestTag(AttendanceTestTags.PREVIOUS_WEEK_BUTTON) and hasClickAction(), useUnmergedTree = true)
    private val nextWeekButton get() = rule.onNode(hasTestTag(AttendanceTestTags.NEXT_WEEK_BUTTON) and hasClickAction(), useUnmergedTree = true)
    private val requestOffButton get() = rule.onNodeWithTag(AttendanceTestTags.REQUEST_OFF_BUTTON)
    private val cancelRequestButton get() = rule.onNodeWithTag(AttendanceTestTags.CANCEL_REQUEST_BUTTON)
    private val weeklyCalendarLoadingIndicator get() = rule.onNodeWithTag(AttendanceTestTags.WEEKLY_CALENDAR_LOADING_INDICATOR)

    private val actionButton get() = rule.onNodeWithTag(AttendanceTestTags.ACTION_BUTTONS)
    private val selectedDateData get() = rule.onNodeWithTag(AttendanceTestTags.SELECTED_DATE_DATA)
    private val actionButtonLoadingIndicator get() = rule.onNodeWithTag(AttendanceTestTags.ACTION_BUTTONS_LOADING_INDICATOR)

    private val landscapeScreen get() = rule.onNodeWithTag(AttendanceTestTags.LANDSCAPE_SCREEN)

    @Test
    fun landscapeAttendanceScreen_whenLoaded_displaysCorrectComponents() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.values.toList())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
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
        }

        landscapeScreen.assertIsDisplayed()
        attendanceHeader.assertIsDisplayed()
        weeklyCalendarWidget.assertIsDisplayed()
        actionButton.assertIsDisplayed()
        selectedDateData.assertIsDisplayed()
    }

    @Test
    fun landscapeAttendanceScreen_whenLoading_showsLoadingState() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val pendingRequests = MutableStateFlow(emptyList<AttendanceRecord>())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(true)
        val isCalendarLoading = MutableStateFlow(true)
        val isPendingListLoading = MutableStateFlow(true)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
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
        }

        weeklyCalendarLoadingIndicator.assertIsDisplayed()
        actionButtonLoadingIndicator.assertIsDisplayed()
    }

    @Test
    fun landscapeAttendanceScreen_weekNavigation_triggersCallbacks() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.values.toList())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        var previousWeekClicked = false
        var nextWeekClicked = false

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
                    isProcessingRequest = isProcessingRequest,
                    isHeaderStatsLoading = isHeaderStatsLoading,
                    isCalendarLoading = isCalendarLoading,
                    isPendingListLoading = isPendingListLoading,
                    isRequestButtonVisible = isRequestButtonVisible,
                    onNewDaySelected = {},
                    onRecordFilterBy = {},
                    onPreviousWeek = {
                        previousWeekClicked = true
                        visibleRange.update { DateRange(it.start.minusWeeks(1), it.end.minusWeeks(1)) }
                    },
                    onNextWeek = {
                        nextWeekClicked = true
                        visibleRange.update { DateRange(it.start.plusWeeks(1), it.end.plusWeeks(1)) }
                    },
                    onRequestOff = {},
                    cancelRequest = {}
                )
            }
        }

        val weekText = rule.onNodeWithTag(AttendanceTestTags.CALENDAR_WEEK_TEXT)
        val startFormatter = DateTimeFormatter.ofPattern("dd/MM")
        val endFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        previousWeekButton.performClick()
        assertTrue(previousWeekClicked)
        weekText.assertTextEquals(
            "${visibleRange.value.start.format(startFormatter)} - ${visibleRange.value.end.format(endFormatter)}"
        )

        nextWeekButton.performClick()
        assertTrue(nextWeekClicked)
        weekText.assertTextEquals(
            "${visibleRange.value.start.format(startFormatter)} - ${visibleRange.value.end.format(endFormatter)}"
        )
    }

    @Test
    fun landscapeAttendanceScreen_requestOffButton_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.values.toList())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        var requestOffClicked = false

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
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
        }

        requestOffButton.performClick()
        assertTrue(requestOffClicked)
    }

    @Test
    fun landscapeAttendanceScreen_cancelRequestButton_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 7)
        )) // Pending
        val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.values.toList())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))
        var cancelRequestClicked = false

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
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
        }

        cancelRequestButton.performClick()
        assertTrue(cancelRequestClicked)
    }

    @Test
    fun landscapeAttendanceScreen_pendingRequestsList_isDisplayed() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 7)
        )) // Pending
        val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.values.toList())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
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
        }

        rule.onNodeWithText("Pending Requests").assertIsDisplayed()
    }

    @Test
    fun landscapeAttendanceScreen_daySelection_triggersCallback() {
        val stats = MutableStateFlow(sampleStats)
        val targetDate = LocalDate.now()
        val selectedDay = MutableStateFlow(
            AttendanceRecord(
                id = "test",
                dayOfMonth = targetDate.dayOfMonth,
                dayOfWeek = targetDate.dayOfWeek.name,
                status = AttendanceStatus.Present
            )
        )
        val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.values.toList())
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val initialStart = targetDate.minusDays((targetDate.dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        var selectedRecord: AttendanceRecord? = null

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 1024
            screenHeightDp = 720
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                LandscapeAttendanceScreenContent(
                    stats = stats,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    pendingRequests = pendingRequests,
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
        }
        rule.onNode(hasTestTag(AttendanceTestTags.CALENDAR_DAY_ITEM) and hasText(targetDate.dayOfMonth.toString())).performClick()
        assertTrue(selectedRecord != null)
    }
}
