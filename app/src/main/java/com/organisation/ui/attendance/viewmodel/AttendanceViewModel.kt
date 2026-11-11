package com.organisation.ui.attendance.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.organisation.LoggerHelper
import com.organisation.domaine.attendance.AttendanceRepository
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.advance
import com.organisation.ui.attendance.model.AttendanceEvent
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.attendance.model.CalenderDisplayMode
import com.organisation.ui.attendance.model.DateRange
import com.organisation.ui.attendance.rangeFor
import com.organisation.ui.attendance.startOfPeriod
import com.organisation.ui.common.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    app: Application,
    private val attendanceRepository: AttendanceRepository
) : BaseViewModel<AttendanceEvent>(app) {

    companion object {
        private const val CLASS_NAME = "AttendanceViewModel"
        private const val TAG = "ATTENDANCE"

        const val STOP_TIMEOUT_MS = 5_000L
        const val CACHE_MONTHS_BEFORE = 5L
        const val CACHE_MONTHS_AFTER = 6L

        const val TRIGGER_MONTHS = 2L

        fun buildCacheWindow(anchor: LocalDate): DateRange = DateRange(
            start = anchor.minusMonths(CACHE_MONTHS_BEFORE).withDayOfMonth(1),
            end = anchor.plusMonths(CACHE_MONTHS_AFTER)
                .with(TemporalAdjusters.lastDayOfMonth())
        ).also {
            LoggerHelper.logger.d(
                TAG,
                "$CLASS_NAME|buildCacheWindow| anchor: $anchor, start: ${it.start}, end: ${it.end}"
            )
        }

        fun createDefaultRecord(date: LocalDate): AttendanceRecord = AttendanceRecord(
            id = 0L,
            dayOfMonth = date.dayOfMonth,
            dayOfWeek = date.dayOfWeek.name,
            monthOfYear = date.monthValue,
            year = date.year,
            status = AttendanceStatus.None
        )
    }

    private val currentYear = MutableStateFlow(
        LocalDate.now().year
    )

    private val _attendingStats = MutableStateFlow(
        listOf<AttendanceStat>()
    )

    val attendingStats: StateFlow<List<AttendanceStat>> = _attendingStats.asStateFlow()

    private val _calenderDisplayMode = MutableStateFlow(CalenderDisplayMode.MONTH)
    val calenderDisplayMode: StateFlow<CalenderDisplayMode> = _calenderDisplayMode.asStateFlow()

    /** First day of the period currently shown (month 1st or week start). */
    private val _viewportStart =
        MutableStateFlow(LocalDate.now().startOfPeriod(calenderDisplayMode.value))
    val viewportStart: StateFlow<LocalDate> = _viewportStart.asStateFlow()

    private val _selectedDay = MutableStateFlow(
        getSelectedDay(null)
    )

    private fun getSelectedDay(currentDay: AttendanceRecord?) = currentDay ?: AttendanceRecord(
        id = 0L,
        dayOfWeek = LocalDate.now().dayOfWeek.name,
        dayOfMonth = LocalDate.now().dayOfMonth,
        year = LocalDate.now().year,
        monthOfYear = LocalDate.now().month.value,
        status = AttendanceStatus.None,
    )
    val selectedDay: StateFlow<AttendanceRecord> = _selectedDay.asStateFlow()

    private val _cacheWindow = MutableStateFlow(buildCacheWindow(_viewportStart.value))
    val cacheWindow: StateFlow<DateRange> = _cacheWindow.asStateFlow()

    val cachedDays: StateFlow<Map<LocalDate, AttendanceRecord>> =
        cacheWindow
            .flatMapLatest { window ->
                attendanceRepository.observeUserAttendanceReportViaRanger(
                    start = window.start,
                    end = window.end
                ).also {
                    LoggerHelper.logger.d(
                        TAG,
                        "$CLASS_NAME|cachedDays| data: $it"
                    )
                }
            }
            .map { rows ->
                rows.associateBy { record -> record.date }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = emptyMap(),
            )

    val visibleRange: StateFlow<DateRange> =
        combine(calenderDisplayMode, _viewportStart) { mode, start ->
            start.rangeFor(mode)
        }.distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = _viewportStart.value.rangeFor(calenderDisplayMode.value),
            )

    val visibleDays: StateFlow<Map<LocalDate, AttendanceRecord>> =
        combine(cachedDays, visibleRange) { cache, range ->
            buildMap {
                var d = range.start
                while (!d.isAfter(range.end)) {
                    val record = cache[d] ?: createDefaultRecord(d)
                    put(d, record)
                    d = d.plusDays(1)
                }
            }
        }.distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = emptyMap(),
            )

    private fun observeAttendingStats() {
        viewModelScope.launch {
            currentYear
                .collectLatest { year ->
                    LoggerHelper.logger.d(
                        TAG,
                        "$CLASS_NAME|observeAttendingStats|year: $year"
                    )
                    _isHeaderStatsLoading.update { true }
                    combine(
                        attendanceRepository.observeUserAttendanceStatusReport(
                            year = year,
                            attendanceStatus = AttendanceStatus.Present
                        ).map {
                            AttendanceStat(
                                value = "$it",
                                attendanceStatus = AttendanceStatus.Present
                            )
                        },
                        attendanceRepository.observeUserAttendanceStatusReport(
                            year = year,
                            attendanceStatus = AttendanceStatus.Absent
                        ).map {
                            AttendanceStat(
                                value = "$it",
                                attendanceStatus = AttendanceStatus.Absent
                            )
                        },
                        attendanceRepository.observeUserAttendanceStatusReport(
                            year = year,
                            attendanceStatus = AttendanceStatus.Leave
                        ).map {
                            AttendanceStat(
                                value = "$it",
                                attendanceStatus = AttendanceStatus.Leave
                            )
                        },
                        attendanceRepository.observeUserAttendanceStatusReport(
                            year = year,
                            attendanceStatus = AttendanceStatus.InProgress
                        ).map {
                            AttendanceStat(
                                value = "$it",
                                attendanceStatus = AttendanceStatus.InProgress
                            )
                        }
                    ) {
                        it.toList()
                    }.collect { list ->
                        LoggerHelper.logger.d(
                            TAG,
                            "$CLASS_NAME|observeAttendingStats| $list"
                        )
                        _attendingStats.update { list }
                        _isHeaderStatsLoading.update { false }
                    }
                }
        }
    }

    private fun initializeCurrentDay() {
        viewModelScope.launch {
           val day = attendanceRepository.getAttendanceRapport(LocalDate.now())
            LoggerHelper.logger.i(
                TAG,
                "${CLASS_NAME}|initializeCurrentDay|day: $day"
            )
            _selectedDay.update {
                getSelectedDay(day)
            }
        }
    }

    private fun updateCacheWindow() {
        viewModelScope.launch {
            viewportStart.collectLatest { viewport ->
                val cacheWindow = cacheWindow.value
                LoggerHelper.logger.d(
                    TAG,
                    "$CLASS_NAME|updateCacheWindow|viewportStart: $viewport, cacheWindow: $cacheWindow"
                )
                if (!isComfortablyInside(viewport, cacheWindow)) {
                    _cacheWindow.value = buildCacheWindow(viewport)
                }
            }
        }
    }

    private fun isComfortablyInside(viewport: LocalDate, window: DateRange): Boolean {
        val anchor = window.start.plusMonths(CACHE_MONTHS_BEFORE)
        val delta = ChronoUnit.MONTHS.between(
            anchor.withDayOfMonth(1),
            viewport.withDayOfMonth(1),
        )
        return delta >= -TRIGGER_MONTHS &&
                delta <= (CACHE_MONTHS_AFTER - TRIGGER_MONTHS).also {
            LoggerHelper.logger.d(
                TAG,
                "$CLASS_NAME|isComfortablyInside|result: $it, viewport: $viewport, window: $window"
            )
        }
    }


    private val _isProcessingRequest =
        MutableStateFlow(value = false)
    val isProcessingRequest: StateFlow<Boolean> = _isProcessingRequest.asStateFlow()

    private val _isHeaderStatsLoading = MutableStateFlow(value = false)
    val isHeaderStatsLoading: StateFlow<Boolean> = _isHeaderStatsLoading.asStateFlow()

    private val _isCalendarLoading = MutableStateFlow(value = false)
    val isCalendarLoading: StateFlow<Boolean> = _isCalendarLoading.asStateFlow()

    private val _isPendingListLoading = MutableStateFlow(value = false)
    val isPendingListLoading: StateFlow<Boolean> = _isPendingListLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isRequestButtonVisible: StateFlow<Boolean> = selectedDay.map {
        ((it.date >= LocalDate.now()) &&
                (it.status != AttendanceStatus.Holiday && it.status != AttendanceStatus.Weekend)).also {result->
            Log.d("YOUSSEF","new selected day: ${it.date}, isVisible: $result")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = true
    )

    init {
        observeAttendingStats()
        updateCacheWindow()
        initializeCurrentDay()
    }

    override fun handleEvent(event: AttendanceEvent) {
        when (event) {
            is AttendanceEvent.NewDaySelected -> {
                handleNewDaySelectedEvent(event)
            }

            is AttendanceEvent.FilterRecordsBy -> {
                //TODO
                handleFilterRecordsByEvent(event)
            }

            is AttendanceEvent.RequestOff -> {
                handleRequestOffEvent()
            }

            is AttendanceEvent.CancelRequest -> {
                handleCancelRequestEvent()
            }

            AttendanceEvent.NextPeriod -> {
                handleNextPeriodEvent()
            }

            is AttendanceEvent.OnOrientationChanges -> {
                onOrientationChanged(event.newMode)
            }

            AttendanceEvent.PreviousPeriod -> {
                handlePreviousPeriodEvent()
            }
        }
    }

    fun handleNextPeriodEvent() {
        val currentMonth = viewportStart.value
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handleNextPeriodEvent|currentViewportStart: $currentMonth"
        )
        _viewportStart.value = _viewportStart.value.advance(+1, calenderDisplayMode.value)

        if (viewportStart.value.year != currentMonth.year) {
            currentYear.update {
                viewportStart.value.year
            }
        }
    }

    fun handlePreviousPeriodEvent() {
        val currentMonth = viewportStart.value
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handlePreviousPeriodEvent|currentViewportStart: $currentMonth"
        )
        _viewportStart.value = _viewportStart.value.advance(-1, calenderDisplayMode.value)
        if (viewportStart.value.year != currentMonth.year) {
            currentYear.update {
                viewportStart.value.year
            }
        }
    }

    fun onOrientationChanged(newMode: CalenderDisplayMode) {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|onOrientationChanged| currentMode: ${calenderDisplayMode.value}, newMode: $newMode"
        )
        if (newMode == calenderDisplayMode.value) return
        val focus = selectedDay.value.date
        _viewportStart.value = focus.startOfPeriod(newMode)
        _calenderDisplayMode.value = newMode
    }

    private fun handleCancelRequestEvent() {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handleCancelRequestEvent|selected day: ${_selectedDay.value}"
        )
        viewModelScope.launch {
            attendanceRepository.cancelDayOffRequest(selectedDay.value.date)
        }
        val updatedRecord = selectedDay.value.copy(status = AttendanceStatus.None)
        _selectedDay.value = updatedRecord
    }

    private fun handleRequestOffEvent() {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handleRequestOffEvent|selected day: ${_selectedDay.value}"
        )
        //TODO
        //val updatedRecord = selectedDay.value.copy(status = AttendanceStatus.Pending)
        //_selectedDay.value = updatedRecord
    }

    private fun handleFilterRecordsByEvent(event: AttendanceEvent.FilterRecordsBy) {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handleFilterRecordsByEvent|Selected status: ${event.status}"
        )
        //TODO
    }

    private fun handleNewDaySelectedEvent(event: AttendanceEvent.NewDaySelected) {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handleNewDaySelectedEvent|Selected day: ${event.record}"
        )
        _selectedDay.update {
            event.record
        }
        val localDate = event.record.date
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|handleNewDaySelectedEvent|now: ${LocalDate.now()}, local $localDate"
        )
        if (localDate !in visibleRange.value) {
            _viewportStart.value = localDate.startOfPeriod(calenderDisplayMode.value)
            if (viewportStart.value.year != currentYear.value) {
                currentYear.update {
                    viewportStart.value.year
                }
            }
        }
    }
}
