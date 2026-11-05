package com.organisation.ui.attendance

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.component.AttendanceActionButtons
import com.organisation.ui.attendance.component.AttendanceHeaderWithStats
import com.organisation.ui.attendance.component.AttendanceRequestItem
import com.organisation.ui.attendance.component.CalendarWidget
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.theme.AppBackground
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.AppPrimaryColorDark
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
fun PhoneAttendanceScreen(
    modifier: Modifier = Modifier,
    stats: StateFlow<List<AttendanceStat>>,
    month: StateFlow<LocalDate>,
    selectedDayRecord: StateFlow<AttendanceRecord>,
    daysMap: StateFlow<Map<LocalDate, AttendanceRecord>> = MutableStateFlow(emptyMap()),
    isProcessingRequest: StateFlow<Boolean>,
    isHeaderStatsLoading: StateFlow<Boolean> = MutableStateFlow(false),
    isCalendarLoading: StateFlow<Boolean> = MutableStateFlow(false),
    isRequestButtonVisible: StateFlow<Boolean>,
    onNewDaySelected: (AttendanceRecord) -> Unit,
    onRecordFilterBy: (AttendanceStatus) -> Unit,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onRequestOff: () -> Unit,
    cancelRequest: () -> Unit,
    navigateToPendingRequestScreen: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(AttendanceTestTags.PHONE_SCREEN)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) AppBackgroundDark else AppBackground)
        ) {
            AttendanceHeaderWithStats(
                stats = stats,
                isLoading = isHeaderStatsLoading,
                onStatCardClicked = onRecordFilterBy
            )
            CalendarWidget(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                month = month,
                currentDay = selectedDayRecord,
                daysMap = daysMap,
                isLoading = isCalendarLoading,
                onRecordClick = onNewDaySelected,
                onNextMonth = onNextMonth,
                onPreviousMonth = onPreviousMonth
            )
            AttendanceActionButtons(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 4.dp
                ),
                record = selectedDayRecord.collectAsStateWithLifecycle().value,
                isProcessing = isProcessingRequest.collectAsStateWithLifecycle().value,
                isVisible = isRequestButtonVisible,
                onRequestOff = onRequestOff,
                isLoading = isCalendarLoading.collectAsStateWithLifecycle().value,
                cancelRequest = cancelRequest
            )
            AttendanceRequestItem(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                    .testTag(AttendanceTestTags.SELECTED_DATE_DATA),
                record = selectedDayRecord.collectAsStateWithLifecycle().value,
                isLoading = isCalendarLoading.collectAsStateWithLifecycle().value
            )
        }

        if (!isCalendarLoading.collectAsStateWithLifecycle().value) {
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag(AttendanceTestTags.PENDING_REQUESTS_FAB),
                onClick = navigateToPendingRequestScreen,
                containerColor = if (isSystemInDarkTheme()) AppPrimaryColorDark else AppPrimaryColor,
                contentColor = if (isSystemInDarkTheme()) AppPrimaryColor else Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.pending_requests_label)
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OrganisationPreviews
@Composable
fun PhoneAttendanceScreenPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }
    val selectedDay = remember { MutableStateFlow(dummyRecords.getValue(
        LocalDate.of(2026, 9, 2)
    )) }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(false) }
    val isCalendarLoading = remember { MutableStateFlow(false) }
    val isVisible = remember { MutableStateFlow(true) }
    val month = remember { MutableStateFlow(LocalDate.now()) }

    OrganisationTheme {
        Scaffold { _ ->
            PhoneAttendanceScreen(
                stats = stats,
                daysMap = daysMapFlow,
                month = month,
                selectedDayRecord = selectedDay,
                isProcessingRequest = isProcessing,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OrganisationPreviews
@Composable
fun PhoneAttendanceScreenCancelRequestPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }
    val selectedDay =
        remember { MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ).copy(status = AttendanceStatus.Pending)) }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(false) }
    val isCalendarLoading = remember { MutableStateFlow(false) }
    val isVisible = remember { MutableStateFlow(true) }
    val month = remember { MutableStateFlow(LocalDate.now()) }

    OrganisationTheme {
        Scaffold { _ ->
            PhoneAttendanceScreen(
                stats = stats,
                daysMap = daysMapFlow,
                month = month,
                selectedDayRecord = selectedDay,
                isProcessingRequest = isProcessing,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OrganisationPreviews
@Composable
fun PhoneAttendanceScreenActionButtonNotVisiblePreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }
    val selectedDay =
        remember { MutableStateFlow(dummyRecords.getValue(
            LocalDate.of(2026, 9, 2)
        ).copy(status = AttendanceStatus.Pending)) }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(false) }
    val isCalendarLoading = remember { MutableStateFlow(false) }
    val isVisible = remember { MutableStateFlow(false) }
    val month = remember { MutableStateFlow(LocalDate.now()) }

    OrganisationTheme {
        Scaffold { _ ->
            PhoneAttendanceScreen(
                stats = stats,
                daysMap = daysMapFlow,
                selectedDayRecord = selectedDay,
                month = month,
                isProcessingRequest = isProcessing,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                isRequestButtonVisible = isVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun PhoneAttendanceScreenLoadingPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val daysMapFlow = remember { MutableStateFlow(emptyMap<LocalDate, AttendanceRecord>()) }
    val selectedDay = remember {
        MutableStateFlow(
            AttendanceRecord(
                id = "loading_selected",
                dayOfMonth = 1,
                dayOfWeek = "Monday",
                status = AttendanceStatus.None
            )
        )
    }
    val isProcessing = remember { MutableStateFlow(false) }
    val isHeaderStatsLoading = remember { MutableStateFlow(true) }
    val isCalendarLoading = remember { MutableStateFlow(true) }
    val isVisible = remember { MutableStateFlow(true) }
    val month = remember { MutableStateFlow(LocalDate.now()) }

    OrganisationTheme {
        Scaffold { innerPadding ->
            PhoneAttendanceScreen(
                modifier = Modifier.padding(innerPadding),
                stats = stats,
                daysMap = daysMapFlow,
                selectedDayRecord = selectedDay,
                isProcessingRequest = isProcessing,
                isHeaderStatsLoading = isHeaderStatsLoading,
                isCalendarLoading = isCalendarLoading,
                month = month,
                isRequestButtonVisible = isVisible,
                onNewDaySelected = {},
                onRecordFilterBy = {},
                onRequestOff = {},
                cancelRequest = {},
                navigateToPendingRequestScreen = {}
            )
        }
    }
}
