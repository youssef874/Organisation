package com.organisation.ui.attendance

import android.content.res.Configuration
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.organisation.OrganisationPreviews
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.model.AttendanceEvent
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.attendance.model.CalenderDisplayMode
import com.organisation.ui.attendance.model.DateRange
import com.organisation.ui.attendance.viewmodel.AttendanceViewModel
import com.organisation.ui.pendinglist.viewmodel.PendingListViewModel
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate


@Composable
fun AttendanceScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceViewModel = hiltViewModel(),
    pendingListViewModel: PendingListViewModel = hiltViewModel(),
    navigateToPendingRequestScreen: () -> Unit = {},
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isExpandedWidth = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
    )
    val displayMode = when {
        isExpandedWidth -> CalenderDisplayMode.MONTH
        isLandscape -> CalenderDisplayMode.WEEK
        else -> CalenderDisplayMode.MONTH
    }

    LaunchedEffect(displayMode) {
        viewModel.onEvent(AttendanceEvent.OnOrientationChanges(displayMode))
    }

    AttendanceScreenContent(
        modifier = modifier,
        stats = viewModel.attendingStats,
        month = viewModel.viewportStart,
        visibleRange = viewModel.visibleRange,
        selectedDay = viewModel.selectedDay,
        daysMap = viewModel.visibleDays,
        pendingRequests = pendingListViewModel.pendingList,
        isProcessingRequest = viewModel.isProcessingRequest,
        isHeaderStatsLoading = viewModel.isHeaderStatsLoading,
        isCalendarLoading = viewModel.isCalendarLoading,
        isPendingListLoading = viewModel.isPendingListLoading,
        isRequestButtonVisible = viewModel.isRequestButtonVisible,
        onNewDaySelected = { viewModel.onEvent(AttendanceEvent.NewDaySelected(it)) },
        onRecordFilterBy = { viewModel.onEvent(AttendanceEvent.FilterRecordsBy(it)) },
        onPreviousMonth = { viewModel.onEvent(AttendanceEvent.PreviousPeriod) },
        onNextMonth = { viewModel.onEvent(AttendanceEvent.NextPeriod) },
        onPreviousWeek = { viewModel.onEvent(AttendanceEvent.PreviousPeriod) },
        onNextWeek = { viewModel.onEvent(AttendanceEvent.NextPeriod) },
        onRequestOff = { viewModel.onEvent(AttendanceEvent.RequestOff) },
        cancelRequest = { viewModel.onEvent(AttendanceEvent.CancelRequest) },
        navigateToPendingRequestScreen = navigateToPendingRequestScreen,
    )
}

@Composable
fun AttendanceScreenContent(
    modifier: Modifier = Modifier,
    stats: StateFlow<List<AttendanceStat>>,
    month: StateFlow<LocalDate>,
    visibleRange: StateFlow<DateRange>,
    selectedDay: StateFlow<AttendanceRecord>,
    daysMap: StateFlow<Map<LocalDate, AttendanceRecord>> = MutableStateFlow(emptyMap()),
    pendingRequests: StateFlow<List<AttendanceRecord>> = MutableStateFlow(emptyList()),
    isProcessingRequest: StateFlow<Boolean>,
    isHeaderStatsLoading: StateFlow<Boolean> = MutableStateFlow(false),
    isCalendarLoading: StateFlow<Boolean> = MutableStateFlow(false),
    isPendingListLoading: StateFlow<Boolean> = MutableStateFlow(false),
    isRequestButtonVisible: StateFlow<Boolean>,
    onNewDaySelected: (AttendanceRecord) -> Unit,
    onRecordFilterBy: (AttendanceStatus) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onRequestOff: () -> Unit,
    cancelRequest: () -> Unit,
    navigateToPendingRequestScreen: () -> Unit,
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isExpandedWidth = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
        WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
    )

    when {
        isExpandedWidth -> {
            TabletAttendanceScreenContent(
                modifier = modifier,
                stats = stats,
                month = month,
                selectedDay = selectedDay,
                daysMap = daysMap,
                pendingRequests = pendingRequests,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = onNewDaySelected,
                onRecordFilterBy = onRecordFilterBy,
                onNextMonth = onNextMonth,
                onPreviousMonth = onPreviousMonth,
                onRequestOff = onRequestOff,
                cancelRequest = cancelRequest,
            )
        }
        isLandscape -> {
            LandscapeAttendanceScreenContent(
                modifier = modifier,
                stats = stats,
                visibleRange = visibleRange,
                selectedDay = selectedDay,
                daysMap = daysMap,
                pendingRequests = pendingRequests,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isPendingListLoading = isPendingListLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = onNewDaySelected,
                onRecordFilterBy = onRecordFilterBy,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                onRequestOff = onRequestOff,
                cancelRequest = cancelRequest,
            )
        }
        else -> {
            PhoneAttendanceScreen(
                modifier = modifier,
                stats = stats,
                month = month,
                selectedDayRecord = selectedDay,
                daysMap = daysMap,
                isProcessingRequest = isProcessingRequest,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isRequestButtonVisible,
                onNewDaySelected = onNewDaySelected,
                onRecordFilterBy = onRecordFilterBy,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onRequestOff = onRequestOff,
                cancelRequest = cancelRequest,
                navigateToPendingRequestScreen = navigateToPendingRequestScreen,
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceScreenContentPreview() {
    val stats = MutableStateFlow(sampleStats)
    val selectedDay = MutableStateFlow(dummyRecords.getValue(
        LocalDate.of(2026, 9, 2)
    ))
    val daysMapFlow = MutableStateFlow(dummyRecords)
    val pendingRequests = MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.map { it.value })
    val isProcessingRequest = MutableStateFlow(value = false)
    val isHeaderStatsLoading = MutableStateFlow(value = false)
    val isCalendarLoading = MutableStateFlow(value = false)
    val isPendingListLoading = MutableStateFlow(value = false)
    val isRequestButtonVisible = MutableStateFlow(value = true)
    val month = MutableStateFlow(LocalDate.now())
    val initialStartWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    val visibleRange = MutableStateFlow(DateRange(initialStartWeek, initialStartWeek.plusDays(6)))

    OrganisationTheme {
        AttendanceScreenContent(
            stats = stats,
            month = month,
            visibleRange = visibleRange,
            selectedDay = selectedDay,
            daysMap = daysMapFlow,
            pendingRequests = pendingRequests,
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
            navigateToPendingRequestScreen = {},
        )
    }
}

@OrganisationPreviews
@Composable
fun AttendanceScreenContentLoadingPreview() {
    val stats = MutableStateFlow(sampleStats)
    val selectedDay = MutableStateFlow(dummyRecords.getValue(
        LocalDate.of(2026, 9, 2)
    ))
    val daysMapFlow = MutableStateFlow(emptyMap<LocalDate, AttendanceRecord>())
    val pendingRequests = MutableStateFlow(emptyList<AttendanceRecord>())
    val isProcessingRequest = MutableStateFlow(value = false)
    val isHeaderStatsLoading = MutableStateFlow(value = true)
    val isCalendarLoading = MutableStateFlow(value = true)
    val isPendingListLoading = MutableStateFlow(value = true)
    val isRequestButtonVisible = MutableStateFlow(value = true)
    val month = MutableStateFlow(LocalDate.now())
    val initialStartWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    val visibleRange = MutableStateFlow(DateRange(initialStartWeek, initialStartWeek.plusDays(6)))

    OrganisationTheme {
        AttendanceScreenContent(
            stats = stats,
            month = month,
            visibleRange = visibleRange,
            selectedDay = selectedDay,
            daysMap = daysMapFlow,
            pendingRequests = pendingRequests,
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
            navigateToPendingRequestScreen = {},
        )
    }
}
