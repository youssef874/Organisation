package com.organisation.ui.attendance

import android.app.Application
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.organisation.domaine.attendance.FakeAttendanceRepository
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.attendance.model.CalenderDisplayMode
import com.organisation.ui.attendance.viewmodel.AttendanceViewModel
import com.organisation.ui.pendinglist.viewmodel.PendingListViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class AttendanceTestWithViewModel {

    private val context = ApplicationProvider.getApplicationContext<Application>()

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun attendanceScreen_withViewModel_displaysHeaderStatsForCurrentYear() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        val headerAttendance = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_HEADER)
        val attendanceHeaderRow = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_ROW)

        headerAttendance.assertIsDisplayed()
        attendanceHeaderRow.assertIsDisplayed()
        attendanceHeaderRow.onChildAt(0).assert(hasText("Present", substring = true))
        attendanceHeaderRow.onChildAt(0).assert(hasText("12", substring = true))

        attendanceHeaderRow.onChildAt(1).assert(hasText("Absent", substring = true))
        attendanceHeaderRow.onChildAt(1).assert(hasText("2", substring = true))

        attendanceHeaderRow.onChildAt(2).assert(hasText("Leave", substring = true))
        attendanceHeaderRow.onChildAt(2).assert(hasText("3", substring = true))

        attendanceHeaderRow.onChildAt(3).assert(hasText("progress", substring = true))
        attendanceHeaderRow.onChildAt(3).assert(hasText("1", substring = true))
    }

    @Test
    fun attendanceScreen_navigatingYear_updatesHeaderStatsInUI() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        repeat(12) {
            rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON).performClick()
        }

        rule.awaitIdle()

        val headerAttendance = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_HEADER)
        val attendanceHeaderRow = rule.onNodeWithTag(AttendanceTestTags.ATTENDANCE_ROW)

        headerAttendance.assertIsDisplayed()
        attendanceHeaderRow.assertIsDisplayed()
        attendanceHeaderRow.onChildAt(0).assert(hasText("Present", substring = true))
        attendanceHeaderRow.onChildAt(0).assert(hasText("0", substring = true))

        attendanceHeaderRow.onChildAt(1).assert(hasText("Absent", substring = true))
        attendanceHeaderRow.onChildAt(1).assert(hasText("0", substring = true))

        attendanceHeaderRow.onChildAt(2).assert(hasText("Leave", substring = true))
        attendanceHeaderRow.onChildAt(2).assert(hasText("0", substring = true))

        attendanceHeaderRow.onChildAt(3).assert(hasText("progress", substring = true))
        attendanceHeaderRow.onChildAt(3).assert(hasText("0", substring = true))
    }

    @Test
    fun attendanceScreen_calendarWidget_displaysCorrectMonthAndNavigatesSmoothly() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        val currentMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        val currentYearMonth = YearMonth.now()

        // 1. Calendar widget, grid, and current month header are visible and correct
        rule.onNodeWithTag(AttendanceTestTags.CALENDAR_WIDGET).assertIsDisplayed()
        rule.onNodeWithTag(AttendanceTestTags.CALENDAR_GRID).assertIsDisplayed()
        rule.onNodeWithTag(AttendanceTestTags.CALENDAR_MONTH_TEXT).assert(hasText(currentMonthYear))

        // 2. Assert that visible data consumed by the calendar is strictly for the current month
        val initialVisibleDays = viewModel.visibleDays.first { it.isNotEmpty() }
        assertFalse("Visible days should not be empty", initialVisibleDays.isEmpty())
        assertEquals(currentYearMonth.lengthOfMonth(), initialVisibleDays.size)
        initialVisibleDays.keys.forEach { date ->
            assertEquals(currentYearMonth.year, date.year)
            assertEquals(currentYearMonth.monthValue, date.monthValue)
        }

        // Navigate forward by clicking Next Month
        rule.onNodeWithTag(AttendanceTestTags.NEXT_MONTH_BUTTON).performClick()
        rule.awaitIdle()

        val nextYearMonth = currentYearMonth.plusMonths(1)
        val nextMonthYear = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        rule.onNodeWithTag(AttendanceTestTags.CALENDAR_MONTH_TEXT).assert(hasText(nextMonthYear))

        // Assert that visible data updated to the next month
        val nextVisibleDays = viewModel.visibleDays.value
        assertEquals(nextYearMonth.lengthOfMonth(), nextVisibleDays.size)
        nextVisibleDays.keys.forEach { date ->
            assertEquals(nextYearMonth.year, date.year)
            assertEquals(nextYearMonth.monthValue, date.monthValue)
        }

        // Navigate backward by clicking Previous Month
        rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON).performClick()
        rule.awaitIdle()

        rule.onNodeWithTag(AttendanceTestTags.CALENDAR_MONTH_TEXT).assert(hasText(currentMonthYear))

        // Assert that visible data returned to the current month
        val returnedVisibleDays = viewModel.visibleDays.value
        assertEquals(currentYearMonth.lengthOfMonth(), returnedVisibleDays.size)
        returnedVisibleDays.keys.forEach { date ->
            assertEquals(currentYearMonth.year, date.year)
            assertEquals(currentYearMonth.monthValue, date.monthValue)
        }
    }

    @Test
    fun attendanceScreen_monthMode_navigatingNextAndPreviousMonth_updatesVisibleData() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        val currentYearMonth = YearMonth.now()

        rule.onNodeWithTag(AttendanceTestTags.CALENDAR_WIDGET).assertIsDisplayed()

        // Navigate forward by clicking Next Month
        rule.onNodeWithTag(AttendanceTestTags.NEXT_MONTH_BUTTON).performClick()
        rule.awaitIdle()

        val nextYearMonth = currentYearMonth.plusMonths(1)
        val nextVisibleDays = viewModel.visibleDays.value
        assertEquals(nextYearMonth.lengthOfMonth(), nextVisibleDays.size)
        nextVisibleDays.keys.forEach { date ->
            assertEquals(nextYearMonth.year, date.year)
            assertEquals(nextYearMonth.monthValue, date.monthValue)
        }
        assertFalse(nextVisibleDays.containsKey(nextYearMonth.atDay(1).minusDays(1)))
        assertFalse(nextVisibleDays.containsKey(nextYearMonth.atEndOfMonth().plusDays(1)))

        // Navigate backward by clicking Previous Month
        rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON).performClick()
        rule.awaitIdle()

        val returnedVisibleDays = viewModel.visibleDays.value
        assertEquals(currentYearMonth.lengthOfMonth(), returnedVisibleDays.size)
        returnedVisibleDays.keys.forEach { date ->
            assertEquals(currentYearMonth.year, date.year)
            assertEquals(currentYearMonth.monthValue, date.monthValue)
        }
        assertFalse(returnedVisibleDays.containsKey(currentYearMonth.atDay(1).minusDays(1)))
        assertFalse(returnedVisibleDays.containsKey(currentYearMonth.atEndOfMonth().plusDays(1)))
    }

    @Test
    fun attendanceScreen_switchingToWeekMode_displaysWeeklyCalendarWidget() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.setOrientationLandscape()
        try {
            rule.setContent {
                AttendanceScreen(
                    viewModel = viewModel,
                    pendingListViewModel = pendingListViewModel
                )
            }

            rule.awaitIdle()

            // Verify Weekly Calendar Widget is now visible
            rule.onNodeWithTag(AttendanceTestTags.WEEKLY_CALENDAR_WIDGET).assertIsDisplayed()

            // Assert that visible data corresponds to the week data only (7 days within visibleRange)
            val initialVisibleDays = viewModel.visibleDays.first { it.size == 7 }
            assert(viewModel.calenderDisplayMode.value == CalenderDisplayMode.WEEK)
            assertFalse("Visible days should not be empty", initialVisibleDays.isEmpty())
            assertEquals(7, initialVisibleDays.size)
            val initialWeekRange = viewModel.visibleRange.value
            initialVisibleDays.keys.forEach { date ->
                assertTrue("Date $date should be within week range $initialWeekRange", date in initialWeekRange)
            }
            assertFalse(initialVisibleDays.containsKey(initialWeekRange.start.minusDays(1)))
            assertFalse(initialVisibleDays.containsKey(initialWeekRange.end.plusDays(1)))

        } finally {
            uiDevice.setOrientationNatural()
        }
    }

    @Test
    fun attendanceScreen_weekMode_navigatingNextAndPreviousWeek_updatesVisibleData() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.setOrientationLandscape()
        try {
            rule.setContent {
                AttendanceScreen(
                    viewModel = viewModel,
                    pendingListViewModel = pendingListViewModel
                )
            }

            rule.awaitIdle()

            // Ensure initial week view is displayed
            rule.onNodeWithTag(AttendanceTestTags.WEEKLY_CALENDAR_WIDGET).assertIsDisplayed()
            viewModel.visibleDays.first { it.size == 7 }

            // Test week navigation affordances and verify visible data updates to next week
            rule.onNodeWithTag(AttendanceTestTags.NEXT_WEEK_BUTTON).performClick()
            rule.awaitIdle()

            val nextWeekRange = viewModel.visibleRange.value
            val nextVisibleDays = viewModel.visibleDays.value
            assertEquals(7, nextVisibleDays.size)
            nextVisibleDays.keys.forEach { date ->
                assertTrue("Date $date should be within next week range $nextWeekRange", date in nextWeekRange)
            }

            assertFalse(nextVisibleDays.containsKey(nextWeekRange.start.minusDays(1)))
            assertFalse(nextVisibleDays.containsKey(nextWeekRange.end.plusDays(1)))

            // Test previous week navigation
            rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_WEEK_BUTTON).performClick()
            rule.awaitIdle()

            val returnedWeekRange = viewModel.visibleRange.value
            val returnedVisibleDays = viewModel.visibleDays.value
            assertEquals(7, returnedVisibleDays.size)
            returnedVisibleDays.keys.forEach { date ->
                assertTrue("Date $date should be within returned week range $returnedWeekRange", date in returnedWeekRange)
            }

            assertFalse(returnedVisibleDays.containsKey(returnedWeekRange.start.minusDays(1)))
            assertFalse(returnedVisibleDays.containsKey(returnedWeekRange.end.plusDays(1)))

            rule.onNodeWithTag(AttendanceTestTags.WEEKLY_CALENDAR_WIDGET).assertIsDisplayed()
        } finally {
            uiDevice.setOrientationNatural()
        }
    }

    @Test
    fun attendanceScreen_initialState_displaysCurrentDayAsSelected() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        val today = LocalDate.now()
        val selected = viewModel.selectedDay.value
        assertEquals(today.year, selected.year)
        assertEquals(today.monthValue, selected.monthOfYear)
        assertEquals(today.dayOfMonth, selected.dayOfMonth)
    }

    @Test
    fun attendanceScreen_selectingNewDay_updatesSelectedDay() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        val targetDay = 15
        val currentYearMonth = YearMonth.now()
        val expectedDate = currentYearMonth.atDay(targetDay)

        rule.onAllNodes(hasTestTag(AttendanceTestTags.CALENDAR_DAY_ITEM))[targetDay - 1].performClick()

        rule.awaitIdle()

        val selected = viewModel.selectedDay.value
        assertEquals(expectedDate.year, selected.year)
        assertEquals(expectedDate.monthValue, selected.monthOfYear)
        assertEquals(expectedDate.dayOfMonth, selected.dayOfMonth)
    }

    @Test
    fun attendanceScreen_requestButton_visibilityUpdatesBasedOnSelectedDay() = runTest {
        val repository = FakeAttendanceRepository()
        val viewModel = AttendanceViewModel(context, repository)
        val pendingListViewModel = PendingListViewModel(context)

        rule.setContent {
            AttendanceScreen(
                viewModel = viewModel,
                pendingListViewModel = pendingListViewModel
            )
        }

        rule.awaitIdle()

        var isVisible = viewModel.isRequestButtonVisible.first { it }

        assertTrue(isVisible)
        rule.onNodeWithTag(AttendanceTestTags.REQUEST_OFF_BUTTON).assertIsDisplayed()

        // Navigate to previous month to select a past date via UI click
        rule.onNodeWithTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON).performClick()
        rule.awaitIdle()

        // Click the first day of the previous month (guaranteed to be in the past)
        rule.onAllNodes(hasTestTag(AttendanceTestTags.CALENDAR_DAY_ITEM))[0].performClick()
        rule.awaitIdle()

        isVisible = viewModel.isRequestButtonVisible.first { !it }

        assertFalse(isVisible)
        rule.onNodeWithTag(AttendanceTestTags.REQUEST_OFF_BUTTON).assertDoesNotExist()
    }
}


