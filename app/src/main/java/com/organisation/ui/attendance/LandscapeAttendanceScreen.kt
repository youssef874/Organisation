package com.organisation.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.component.AttendanceActionButtons
import com.organisation.ui.attendance.component.AttendanceRequestItem
import com.organisation.ui.attendance.component.HorizontalAttendanceHeaderWithStats
import com.organisation.ui.attendance.component.WeeklyCalendarWidget
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.attendance.model.DateRange
import com.organisation.ui.pendinglist.component.AttendanceRequestList
import com.organisation.ui.theme.AppBackground
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppComponentsBorder
import com.organisation.ui.theme.AppComponentsBorderDark
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate


@Composable
fun LandscapeAttendanceScreenContent(
    modifier: Modifier = Modifier,
    stats: StateFlow<List<AttendanceStat>>,
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
    onPreviousWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {},
    onRequestOff: () -> Unit,
    cancelRequest: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(AttendanceTestTags.LANDSCAPE_SCREEN)
            .background(if (isSystemInDarkTheme()) AppBackgroundDark else AppBackground)
    ) {

        HorizontalAttendanceHeaderWithStats(
            stats = stats,
            isLoading = isHeaderStatsLoading,
            onStatCardClicked = onRecordFilterBy
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val calendarLoading by isCalendarLoading.collectAsStateWithLifecycle()
                    WeeklyCalendarWidget(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 4.dp
                        ),
                        visibleRange = visibleRange,
                        currentDay = selectedDay,
                        daysMap = daysMap,
                        isLoading = isCalendarLoading,
                        onRecordClick = onNewDaySelected,
                        onNextWeek = onNextWeek,
                        onPreviousWeek = onPreviousWeek
                    )
                    AttendanceActionButtons(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 4.dp
                        ),
                        record = selectedDay.collectAsStateWithLifecycle().value,
                        isProcessing = isProcessingRequest.collectAsStateWithLifecycle().value,
                        isVisible = isRequestButtonVisible,
                        onRequestOff = onRequestOff,
                        isLoading = calendarLoading,
                        cancelRequest = cancelRequest
                    )
                }
            }
            VerticalDivider(
                color = if (isSystemInDarkTheme()) AppComponentsBorderDark else AppComponentsBorder
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                val pendingList by pendingRequests.collectAsStateWithLifecycle()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AttendanceRequestItem(
                        modifier = Modifier.testTag(AttendanceTestTags.SELECTED_DATE_DATA),
                        record = selectedDay.collectAsStateWithLifecycle().value,
                        isLoading = isCalendarLoading.collectAsStateWithLifecycle().value
                    )

                    val pendingListLoading by isPendingListLoading.collectAsStateWithLifecycle()

                    if (pendingList.isNotEmpty() || pendingListLoading) {
                        Column {
                            Text(
                                text = stringResource(R.string.pending_requests_label),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                            Spacer(Modifier.height(6.dp))
                            AttendanceRequestList(
                                requests = pendingList,
                                isLoading = pendingListLoading,
                                useCardStyle = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun LandscapeAttendanceScreenPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMap = remember { MutableStateFlow(dummyRecords) }
    val pendingRequests =
        remember {
            MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }
                .map { it.value })
        }
    val selectedDay = remember {
        MutableStateFlow(
            dummyRecords.getValue(
                LocalDate.of(2026, 9, 2)
            )
        )
    }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(false) }
    val isCalendarLoading = remember { MutableStateFlow(false) }
    val isPendingListLoading = remember { MutableStateFlow(false) }
    val isVisible = remember { MutableStateFlow(true) }
    val initialStartWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    val visibleRange =
        remember { MutableStateFlow(DateRange(initialStartWeek, initialStartWeek.plusDays(6))) }

    OrganisationTheme {
        LandscapeAttendanceScreenContent(
            stats = stats,
            daysMap = daysMap,
            pendingRequests = pendingRequests,
            visibleRange = visibleRange,
            selectedDay = selectedDay,
            isProcessingRequest = isProcessing,
            isHeaderStatsLoading = isHeaderStatsLoading,
            isCalendarLoading = isCalendarLoading,
            isPendingListLoading = isPendingListLoading,
            isRequestButtonVisible = isVisible,
            onNewDaySelected = {},
            onRecordFilterBy = {},
            onRequestOff = {},
            cancelRequest = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun LandscapeAttendanceScreenLoadingPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMap = remember { MutableStateFlow(emptyMap<LocalDate, AttendanceRecord>()) }
    val pendingRequests = remember { MutableStateFlow(emptyList<AttendanceRecord>()) }
    val selectedDay = remember {
        MutableStateFlow(
            AttendanceRecord(
                id = 1L,
                dayOfMonth = 1,
                dayOfWeek = "Monday",
                status = AttendanceStatus.None
            )
        )
    }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(true) }
    val isCalendarLoading = remember { MutableStateFlow(true) }
    val isPendingListLoading = remember { MutableStateFlow(true) }
    val isVisible = remember { MutableStateFlow(true) }
    val initialStartWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    val visibleRange =
        remember { MutableStateFlow(DateRange(initialStartWeek, initialStartWeek.plusDays(6))) }

    OrganisationTheme {
        LandscapeAttendanceScreenContent(
            stats = stats,
            daysMap = daysMap,
            pendingRequests = pendingRequests,
            visibleRange = visibleRange,
            selectedDay = selectedDay,
            isProcessingRequest = isProcessing,
            isHeaderStatsLoading = isHeaderStatsLoading,
            isCalendarLoading = isCalendarLoading,
            isPendingListLoading = isPendingListLoading,
            isRequestButtonVisible = isVisible,
            onNewDaySelected = {},
            onRecordFilterBy = {},
            onRequestOff = {},
            cancelRequest = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun LandscapeAttendanceScreenCancelRequestPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMap = remember { MutableStateFlow(dummyRecords) }
    val pendingRequests =
        remember {
            MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }
                .map { it.value })
        }
    val selectedDay =
        remember {
            MutableStateFlow(
                dummyRecords.getValue(
                    LocalDate.of(2026, 9, 2)
                ).copy(status = AttendanceStatus.Pending)
            )
        }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(false) }
    val isCalendarLoading = remember { MutableStateFlow(false) }
    val isPendingListLoading = remember { MutableStateFlow(false) }
    val isVisible = remember { MutableStateFlow(true) }
    val initialStartWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    val visibleRange =
        remember { MutableStateFlow(DateRange(initialStartWeek, initialStartWeek.plusDays(6))) }

    OrganisationTheme {
        LandscapeAttendanceScreenContent(
            stats = stats,
            daysMap = daysMap,
            pendingRequests = pendingRequests,
            visibleRange = visibleRange,
            selectedDay = selectedDay,
            isProcessingRequest = isProcessing,
            isHeaderStatsLoading = isHeaderStatsLoading,
            isCalendarLoading = isCalendarLoading,
            isPendingListLoading = isPendingListLoading,
            isRequestButtonVisible = isVisible,
            onNewDaySelected = {},
            onRecordFilterBy = {},
            onRequestOff = {},
            cancelRequest = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun LandscapeAttendanceScreenDarkPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMap = remember { MutableStateFlow(dummyRecords) }
    val pendingRequests =
        remember { MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending || it.value.status == AttendanceStatus.Leave }.map { it.value }) }
    val selectedDay = remember { MutableStateFlow(dummyRecords.getValue(
        LocalDate.of(2026, 9, 2)
    )) }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(false) }
    val isCalendarLoading = remember { MutableStateFlow(false) }
    val isPendingListLoading = remember { MutableStateFlow(false) }
    val isVisible = remember { MutableStateFlow(true) }
    val initialStartWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
    val visibleRange =
        remember { MutableStateFlow(DateRange(initialStartWeek, initialStartWeek.plusDays(6))) }

    OrganisationTheme(darkTheme = true) {
        LandscapeAttendanceScreenContent(
            stats = stats,
            daysMap = daysMap,
            pendingRequests = pendingRequests,
            visibleRange = visibleRange,
            selectedDay = selectedDay,
            isProcessingRequest = isProcessing,
            isHeaderStatsLoading = isHeaderStatsLoading,
            isCalendarLoading = isCalendarLoading,
            isPendingListLoading = isPendingListLoading,
            isRequestButtonVisible = isVisible,
            onNewDaySelected = {},
            onRecordFilterBy = {},
            onRequestOff = {},
            cancelRequest = {}
        )
    }
}
