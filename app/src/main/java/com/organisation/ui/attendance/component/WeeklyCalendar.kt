package com.organisation.ui.attendance.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.attendance.model.DateRange
import com.organisation.ui.common.shimmerEffect
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppCardSurfaceColorDark
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyCalendarWidget(
    modifier: Modifier = Modifier,
    visibleRange: StateFlow<DateRange>,
    currentDay: StateFlow<AttendanceRecord>,
    daysMap: StateFlow<Map<LocalDate, AttendanceRecord>> = MutableStateFlow(emptyMap()),
    isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    onRecordClick: (AttendanceRecord) -> Unit,
    onPreviousWeek: () -> Unit = {},
    onNextWeek: () -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    val loading by isLoading.collectAsStateWithLifecycle()
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppCardSurfaceColorDark else Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.WEEKLY_CALENDAR_WIDGET),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (loading) {
                WeeklyCalendarSkeleton()
            } else {
                WeeklyCalendarNavigator(
                    visibleRange = visibleRange,
                    onPreviousWeek = onPreviousWeek,
                    onNextWeek = onNextWeek
                )
                Spacer(Modifier.height(12.dp))
                CalendarHeader()
                Spacer(Modifier.height(8.dp))
                WeeklyCalendarGrid(
                    visibleRange = visibleRange,
                    currentDay = currentDay,
                    daysMap = daysMap,
                    onRecordClick = onRecordClick
                )
            }
        }
    }
}

@Composable
fun WeeklyCalendarNavigator(
    modifier: Modifier = Modifier,
    visibleRange: StateFlow<DateRange>,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val range by visibleRange.collectAsStateWithLifecycle()
    val weekRangeText = remember(range) {
        val startFormatter = DateTimeFormatter.ofPattern("dd/MM")
        val endFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        "${range.start.format(startFormatter)} - ${range.end.format(endFormatter)}"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousWeek,
            modifier = Modifier.testTag(AttendanceTestTags.PREVIOUS_WEEK_BUTTON)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_week_label),
                tint = if (isDarkMode) Color.White else AppPrimaryColor
            )
        }

        Text(
            modifier = Modifier.testTag(AttendanceTestTags.CALENDAR_WEEK_TEXT),
            text = weekRangeText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else Color.Black
        )

        IconButton(
            onClick = onNextWeek,
            modifier = Modifier.testTag(AttendanceTestTags.NEXT_WEEK_BUTTON)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.next_week_label),
                tint = if (isDarkMode) Color.White else AppPrimaryColor
            )
        }
    }
}

@Composable
fun WeeklyCalendarGrid(
    modifier: Modifier = Modifier,
    visibleRange: StateFlow<DateRange>,
    currentDay: StateFlow<AttendanceRecord>,
    daysMap: StateFlow<Map<LocalDate, AttendanceRecord>>,
    onRecordClick: (AttendanceRecord) -> Unit
) {
    val range by visibleRange.collectAsStateWithLifecycle()
    val currentDayRecord by currentDay.collectAsStateWithLifecycle()
    val attendanceMap by daysMap.collectAsStateWithLifecycle()

    val weekDays = remember(range) {
        (0..6).map { range.start.plusDays(it.toLong()) }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(weekDays, key = { it.toEpochDay() }) { date ->
            val record = attendanceMap[date] ?: AttendanceRecord(
                id = "${date.year}_${date.monthValue}_${date.dayOfMonth}",
                dayOfMonth = date.dayOfMonth,
                dayOfWeek = date.dayOfWeek.name,
                monthOfYear = date.monthValue,
                year = date.year,
                status = AttendanceStatus.None
            )
            val isSelected = date == currentDayRecord.date

            Box(modifier = Modifier.fillParentMaxWidth(1f / 7f)) {
                CalendarDayItem(
                    dayOfMonth = date.dayOfMonth,
                    status = record.status,
                    isSelected = isSelected,
                    onClick = {
                        onRecordClick(record)
                    }
                )
            }
        }
    }
}

@Composable
fun WeeklyCalendarSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.WEEKLY_CALENDAR_LOADING_INDICATOR)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .size(120.dp, 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(7) {
                Box(
                    modifier = Modifier
                        .size(30.dp, 12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .shimmerEffect()
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            repeat(7) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun WeeklyCalendarWidgetPreview() {
    val selectedRecord = remember { mutableStateOf(dummyRecords.getValue(
        LocalDate.of(2026, 9, 2)
    )) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val startOfWeek = selectedDate.value.minusDays((selectedDate.value.dayOfWeek.value - 1).toLong())
    val rangeFlow = remember { MutableStateFlow(DateRange(startOfWeek, startOfWeek.plusDays(6))) }
    val dayFlow = remember { MutableStateFlow(selectedRecord.value) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }

    OrganisationTheme {
        WeeklyCalendarWidget(
            visibleRange = rangeFlow,
            currentDay = dayFlow,
            daysMap = daysMapFlow,
            onRecordClick = {
                selectedRecord.value = it
            },
            onPreviousWeek = {
                selectedDate.value = selectedDate.value.minusWeeks(1)
                val newStart = selectedDate.value.minusDays((selectedDate.value.dayOfWeek.value - 1).toLong())
                rangeFlow.value = DateRange(newStart, newStart.plusDays(6))
            },
            onNextWeek = {
                selectedDate.value = selectedDate.value.plusWeeks(1)
                val newStart = selectedDate.value.minusDays((selectedDate.value.dayOfWeek.value - 1).toLong())
                rangeFlow.value = DateRange(newStart, newStart.plusDays(6))
            }
        )
    }
}

@OrganisationPreviews
@Composable
fun WeeklyCalendarWidgetDarkPreview() {
    val selectedRecord = remember { mutableStateOf(dummyRecords.getValue(
        LocalDate.of(2026, 9, 2)
    )) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val startOfWeek = selectedDate.value.minusDays((selectedDate.value.dayOfWeek.value - 1).toLong())
    val rangeFlow = remember { MutableStateFlow(DateRange(startOfWeek, startOfWeek.plusDays(6))) }
    val dayFlow = remember { MutableStateFlow(selectedRecord.value) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }

    OrganisationTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(AppBackgroundDark)
                .padding(16.dp)
        ) {
            WeeklyCalendarWidget(
                visibleRange = rangeFlow,
                currentDay = dayFlow,
                daysMap = daysMapFlow,
                onRecordClick = {
                    selectedRecord.value = it
                },
                onPreviousWeek = {
                    selectedDate.value = selectedDate.value.minusWeeks(1)
                    val newStart = selectedDate.value.minusDays((selectedDate.value.dayOfWeek.value - 1).toLong())
                    rangeFlow.value = DateRange(newStart, newStart.plusDays(6))
                },
                onNextWeek = {
                    selectedDate.value = selectedDate.value.plusWeeks(1)
                    val newStart = selectedDate.value.minusDays((selectedDate.value.dayOfWeek.value - 1).toLong())
                    rangeFlow.value = DateRange(newStart, newStart.plusDays(6))
                }
            )
        }
    }
}
