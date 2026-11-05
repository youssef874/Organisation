package com.organisation.ui.employee.attendance

import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.AttendanceScreenContent
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.attendance.model.DateRange
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AttendanceScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val phoneScreen get() = rule.onNodeWithTag(AttendanceTestTags.PHONE_SCREEN)
    private val landscapeScreen get() = rule.onNodeWithTag(AttendanceTestTags.LANDSCAPE_SCREEN)
    private val tabletScreen get() = rule.onNodeWithTag(AttendanceTestTags.TABLET_SCREEN)

    @Test
    fun attendanceScreen_portraitMode_rendersPhoneScreen() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val records = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_PORTRAIT
            screenWidthDp = 400
            screenHeightDp = 800
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                AttendanceScreenContent(
                    stats = stats,
                    month = month,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    daysMap = records,
                    isProcessingRequest = isProcessingRequest,
                    isHeaderStatsLoading = isHeaderStatsLoading,
                    isCalendarLoading = isCalendarLoading,
                    isPendingListLoading = isPendingListLoading,
                    isRequestButtonVisible = isRequestButtonVisible,
                    onNewDaySelected = {},
                    onRecordFilterBy = {},
                    onPreviousMonth = {},
                    onNextMonth = {},
                    onPreviousWeek = {},
                    onNextWeek = {},
                    onRequestOff = {},
                    cancelRequest = {},
                    navigateToPendingRequestScreen = {}
                )
            }
        }

        phoneScreen.assertIsDisplayed()
    }

    @Test
    fun attendanceScreen_landscapeMode_rendersLandscapeScreen() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val records = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
            screenWidthDp = 800
            screenHeightDp = 400
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                AttendanceScreenContent(
                    stats = stats,
                    month = month,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    daysMap = records,
                    isProcessingRequest = isProcessingRequest,
                    isHeaderStatsLoading = isHeaderStatsLoading,
                    isCalendarLoading = isCalendarLoading,
                    isPendingListLoading = isPendingListLoading,
                    isRequestButtonVisible = isRequestButtonVisible,
                    onNewDaySelected = {},
                    onRecordFilterBy = {},
                    onPreviousMonth = {},
                    onNextMonth = {},
                    onPreviousWeek = {},
                    onNextWeek = {},
                    onRequestOff = {},
                    cancelRequest = {},
                    navigateToPendingRequestScreen = {}
                )
            }
        }

        landscapeScreen.assertIsDisplayed()
    }

    @Test
    fun attendanceScreen_tabletMode_rendersTabletScreen() {
        val stats = MutableStateFlow(sampleStats)
        val selectedDay = MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ))
        val records = MutableStateFlow(dummyRecords)
        val isProcessingRequest = MutableStateFlow(false)
        val isHeaderStatsLoading = MutableStateFlow(false)
        val isCalendarLoading = MutableStateFlow(false)
        val isPendingListLoading = MutableStateFlow(false)
        val isRequestButtonVisible = MutableStateFlow(true)
        val month = MutableStateFlow(LocalDate.now())
        val initialStart = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
        val visibleRange = MutableStateFlow(DateRange(initialStart, initialStart.plusDays(6)))

        val configuration = Configuration().apply {
            orientation = Configuration.ORIENTATION_PORTRAIT
            screenWidthDp = 900 // >= 840dp for expanded width
            screenHeightDp = 1200
        }

        rule.setContent {
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                AttendanceScreenContent(
                    stats = stats,
                    month = month,
                    visibleRange = visibleRange,
                    selectedDay = selectedDay,
                    daysMap = records,
                    isProcessingRequest = isProcessingRequest,
                    isHeaderStatsLoading = isHeaderStatsLoading,
                    isCalendarLoading = isCalendarLoading,
                    isPendingListLoading = isPendingListLoading,
                    isRequestButtonVisible = isRequestButtonVisible,
                    onNewDaySelected = {},
                    onRecordFilterBy = {},
                    onPreviousMonth = {},
                    onNextMonth = {},
                    onPreviousWeek = {},
                    onNextWeek = {},
                    onRequestOff = {},
                    cancelRequest = {},
                    navigateToPendingRequestScreen = {}
                )
            }
        }

        tabletScreen.assertIsDisplayed()
    }
}
