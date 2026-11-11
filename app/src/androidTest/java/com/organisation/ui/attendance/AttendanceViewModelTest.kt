package com.organisation.ui.attendance

import android.app.Application
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.domaine.attendance.FakeAttendanceRepository
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceEvent
import com.organisation.ui.attendance.model.CalenderDisplayMode
import com.organisation.ui.attendance.viewmodel.AttendanceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AttendanceViewModelTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private lateinit var repository: FakeAttendanceRepository
    private lateinit var viewModel: AttendanceViewModel

    @Before
    fun setUp() {
        repository = FakeAttendanceRepository()
        viewModel = AttendanceViewModel(context, repository)
    }

    @Test
    fun initialState_selectedDayRepresentsCurrentDay() = runTest {
        val today = LocalDate.now()
        val selected = viewModel.selectedDay.value
        assertEquals(today.year, selected.year)
        assertEquals(today.monthValue, selected.monthOfYear)
        assertEquals(today.dayOfMonth, selected.dayOfMonth)
    }

    @Test
    fun selectingNewDay_updatesSelectedDay() = runTest {
        val newDate = LocalDate.now().plusDays(1)
        val newRecord = AttendanceRecord(
            id = 1L,
            dayOfMonth = newDate.dayOfMonth,
            dayOfWeek = newDate.dayOfWeek.name,
            monthOfYear = newDate.monthValue,
            year = newDate.year,
            status = AttendanceStatus.Present
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(newRecord))

        val day = viewModel.selectedDay.first { it == newRecord }

        assertEquals(newRecord, day)
    }

    @Test
    fun isRequestButtonVisible_isTrue_forFutureOrTodayDateWithNormalStatus() = runTest {
        val today = LocalDate.now()
        val todayRecord = AttendanceRecord(
            id = 2L,
            dayOfMonth = today.dayOfMonth,
            dayOfWeek = today.dayOfWeek.name,
            monthOfYear = today.monthValue,
            year = today.year,
            status = AttendanceStatus.None
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(todayRecord))

        val visible = viewModel.isRequestButtonVisible.first { it }

        assertTrue(visible)

        val futureDate = LocalDate.now().plusDays(1)
        val futureRecord = AttendanceRecord(
            id = 3L,
            dayOfMonth = futureDate.dayOfMonth,
            dayOfWeek = futureDate.dayOfWeek.name,
            monthOfYear = futureDate.monthValue,
            year = futureDate.year,
            status = AttendanceStatus.None
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(futureRecord))

        val isVisible = viewModel.isRequestButtonVisible.first { it }

        assertTrue(isVisible)
    }

    @Test
    fun isRequestButtonVisible_isFalse_forPastDate() = runTest {
        val pastDate = LocalDate.now().minusDays(1)
        val record = AttendanceRecord(
            id = 4L,
            dayOfMonth = pastDate.dayOfMonth,
            dayOfWeek = pastDate.dayOfWeek.name,
            monthOfYear = pastDate.monthValue,
            year = pastDate.year,
            status = AttendanceStatus.None
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(record))

        val isVisible = viewModel.isRequestButtonVisible.first { !it }

        Log.d("YOUSSEF","isVisible: $isVisible")
        assertFalse(isVisible)
    }

    @Test
    fun isRequestButtonVisible_isFalse_forHolidayOrWeekend() = runTest {
        val today = LocalDate.now()
        val holidayRecord = AttendanceRecord(
            id = 5L,
            dayOfMonth = today.dayOfMonth,
            dayOfWeek = today.dayOfWeek.name,
            monthOfYear = today.monthValue,
            year = today.year,
            status = AttendanceStatus.Holiday
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(holidayRecord))

        var isVisible = viewModel.isRequestButtonVisible.first { !it }

        assertFalse(isVisible)

        val weekendRecord = AttendanceRecord(
            id = 6L,
            dayOfMonth = today.dayOfMonth,
            dayOfWeek = today.dayOfWeek.name,
            monthOfYear = today.monthValue,
            year = today.year,
            status = AttendanceStatus.Weekend
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(weekendRecord))

        isVisible = viewModel.isRequestButtonVisible.first { !it }

        assertFalse(isVisible)
    }

    @Test
    fun initialState_loadsAttendingStats_andHeaderStatsLoadingIsFalse() = runTest {
        val stats = viewModel.attendingStats.first { it.isNotEmpty() }
        assertEquals(4, stats.size)

        val presentStat = stats.find { it.attendanceStatus == AttendanceStatus.Present }
        val absentStat = stats.find { it.attendanceStatus == AttendanceStatus.Absent }
        val leaveStat = stats.find { it.attendanceStatus == AttendanceStatus.Leave }
        val inProgressStat = stats.find { it.attendanceStatus == AttendanceStatus.InProgress }

        assertEquals("12", presentStat?.value)
        assertEquals("2", absentStat?.value)
        assertEquals("3", leaveStat?.value)
        assertEquals("1", inProgressStat?.value)
        assertFalse(viewModel.isHeaderStatsLoading.value)
    }

    @Test
    fun navigatingMonthsToDifferentYear_updatesYearAndRecomputesAttendingStats() = runTest {
        repeat(12) {
            viewModel.onEvent(AttendanceEvent.PreviousPeriod)
        }

        val updatedViewport = viewModel.viewportStart.first { it.year == 2025 }
        assertEquals(2025, updatedViewport.year)

        val stats = viewModel.attendingStats.first { list ->
            list.isNotEmpty() && list.all { it.value == "0" }
        }
        val presentStat = stats.find { it.attendanceStatus == AttendanceStatus.Present }
        val absentStat = stats.find { it.attendanceStatus == AttendanceStatus.Absent }

        assertEquals("0", presentStat?.value)
        assertEquals("0", absentStat?.value)
        assertFalse(viewModel.isHeaderStatsLoading.value)
    }

    @Test
    fun navigatingWeeksToDifferentYear_updatesYearAndRecomputesAttendingStats() = runTest {
        // Switch to week mode so PreviousPeriod advances by week
        viewModel.onEvent(AttendanceEvent.OnOrientationChanges(CalenderDisplayMode.WEEK))
        viewModel.calenderDisplayMode.first { it == CalenderDisplayMode.WEEK }

        repeat(53) {
            viewModel.onEvent(AttendanceEvent.PreviousPeriod)
        }

        val updatedViewport = viewModel.viewportStart.first { it.year < 2026 }
        assertTrue(updatedViewport.year < 2026)

        val stats = viewModel.attendingStats.first { list ->
            list.isNotEmpty() && list.all { it.value == "0" }
        }
        val presentStat = stats.find { it.attendanceStatus == AttendanceStatus.Present }
        assertEquals("0", presentStat?.value)
        assertFalse(viewModel.isHeaderStatsLoading.value)
    }

    @Test
    fun initialCacheWindow_covers12MonthsAroundViewportStart() = runTest {
        val now = LocalDate.now()
        val cacheWindow = viewModel.cacheWindow.value
        val expectedStart = now.minusMonths(AttendanceViewModel.CACHE_MONTHS_BEFORE).withDayOfMonth(1)
        val expectedEnd = now.plusMonths(AttendanceViewModel.CACHE_MONTHS_AFTER).with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())

        assertEquals(expectedStart, cacheWindow.start)
        assertEquals(expectedEnd, cacheWindow.end)
    }

    @Test
    fun navigatingWithinThreshold_doesNotRecalculateCacheWindow() = runTest {
        val initialCacheWindow = viewModel.cacheWindow.value
        val initialViewport = viewModel.viewportStart.value

        // Advance by 1 month (less than TRIGGER_MONTHS = 2)
        viewModel.onEvent(AttendanceEvent.NextPeriod)

        // Ensure the NextPeriod event was processed and viewport advanced
        val newViewport = viewModel.viewportStart.first { it != initialViewport }
        assertEquals(initialViewport.advance(1, CalenderDisplayMode.MONTH), newViewport)

        // Verify cacheWindow was NOT recalculated (stayed within threshold)
        assertEquals(initialCacheWindow, viewModel.cacheWindow.value)
    }

    @Test
    fun navigatingBeyondThreshold_recalculatesCacheWindowAndUpdatesCachedDays() = runTest(UnconfinedTestDispatcher()) {
        // Collect cachedDays in backgroundScope to keep the WhileSubscribed flow active
        backgroundScope.launch { viewModel.cachedDays.collect() }

        val initialWindow = viewModel.cacheWindow.value
        val initialStart = initialWindow.start
        val initialEnd = initialWindow.end

        // Advancing 5 months: crosses the upper threshold (CACHE_MONTHS_AFTER - TRIGGER_MONTHS = 6 - 2 = 4)
        val shiftMonths = 5L
        repeat(shiftMonths.toInt()) {
            viewModel.onEvent(AttendanceEvent.NextPeriod)
        }

        // Await the asynchronous cacheWindow update triggered by viewportStart collector in viewModelScope
        val updatedWindow = viewModel.cacheWindow.first { it.start != initialStart }
        val updatedStart = updatedWindow.start
        val updatedEnd = updatedWindow.end

        // Verify the cacheWindow shifted exactly according to the sliding window specification
        val expectedNewStart = initialStart.plusMonths(shiftMonths)
        val expectedNewEnd = initialEnd.plusMonths(shiftMonths)
            .with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())

        assertEquals(expectedNewStart, updatedStart)
        assertEquals(expectedNewEnd, updatedEnd)

        // Await cachedDays emission for the new window (contains newly added dates after initialEnd)
        val cached = viewModel.cachedDays.first { days ->
            days.keys.any { it.isAfter(initialEnd) }
        }
        assertFalse("cachedDays should not be empty", cached.isEmpty())

        // All cached dates must be strictly within the newly shifted window [updatedStart .. updatedEnd]
        cached.keys.forEach { date ->
            assertTrue("Cached date $date must be >= updatedStart $updatedStart", !date.isBefore(updatedStart))
            assertTrue("Cached date $date must be <= updatedEnd $updatedEnd", !date.isAfter(updatedEnd))
        }

        // Dates that dropped out behind the shifted window must no longer be in cachedDays
        var checkOldDate = initialStart
        val oldCutoff = updatedStart.minusDays(1)
        while (!checkOldDate.isAfter(oldCutoff)) {
            assertFalse("Old date $checkOldDate outside shifted window must be evicted", cached.containsKey(checkOldDate))
            checkOldDate = checkOldDate.plusDays(1)
        }
    }

    @Test
    fun visibleDays_coversEntireVisibleRange_andProvidesDefaultsForMissingRecords() = runTest(UnconfinedTestDispatcher()) {
        backgroundScope.launch { viewModel.visibleDays.collect() }

        val range = viewModel.visibleRange.value
        val days = viewModel.visibleDays.first { it.isNotEmpty() }

        assertFalse("visibleDays should not be empty", days.isEmpty())

        // Verify each date in the range exists in visibleDays map
        var d = range.start
        while (!d.isAfter(range.end)) {
            val record = days[d]
            assertNotNull("Day $d should have a record in visibleDays", record)

            val cachedRecord = viewModel.cachedDays.value[d]
            if (cachedRecord != null) {
                // If provided by repository/cache, use the record as-is
                assertEquals(cachedRecord, record)
            } else {
                // If repository has no record for this date, ViewModel fills default with None status
                assertEquals(AttendanceStatus.None, record?.status)
            }
            d = d.plusDays(1)
        }
    }

    @Test
    fun switchingOrientation_updatesCalenderDisplayMode_andAdaptsVisibleRange() = runTest(UnconfinedTestDispatcher()) {
        backgroundScope.launch { viewModel.visibleDays.collect() }

        assertEquals(CalenderDisplayMode.MONTH, viewModel.calenderDisplayMode.value)

        // Switch to WEEK mode (e.g. landscape)
        viewModel.onEvent(AttendanceEvent.OnOrientationChanges(CalenderDisplayMode.WEEK))

        // Awaiting visibleDays (the final downstream state) guarantees the entire pipeline has settled
        val days = viewModel.visibleDays.first { it.size == 7 }
        assertEquals(7, days.size)

        assertEquals(CalenderDisplayMode.WEEK, viewModel.calenderDisplayMode.value)

        val weekRange = viewModel.visibleRange.value
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(weekRange.start, weekRange.end) + 1
        assertEquals(7L, daysBetween)
    }

    @Test
    fun selectingDayOutsideVisibleRange_updatesViewportStartToContainSelectedDay() = runTest(UnconfinedTestDispatcher()) {
        val nextMonthDate = LocalDate.now().plusMonths(2).withDayOfMonth(15)
        val record = AttendanceRecord(
            id = 7L,
            dayOfMonth = nextMonthDate.dayOfMonth,
            dayOfWeek = nextMonthDate.dayOfWeek.name,
            monthOfYear = nextMonthDate.monthValue,
            year = nextMonthDate.year,
            status = AttendanceStatus.Present
        )

        viewModel.onEvent(AttendanceEvent.NewDaySelected(record))

        val newViewport = viewModel.viewportStart.first { it == nextMonthDate.withDayOfMonth(1) }

        assertEquals(record, viewModel.selectedDay.value)
        assertEquals(nextMonthDate.withDayOfMonth(1), newViewport)
    }
}

