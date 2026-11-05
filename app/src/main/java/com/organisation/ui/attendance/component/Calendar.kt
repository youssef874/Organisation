package com.organisation.ui.attendance.component


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.common.getColor
import com.organisation.ui.common.shimmerEffect
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppButtonColorDark
import com.organisation.ui.theme.AppCalendarRegularTextColor
import com.organisation.ui.theme.AppCalendarTextWeekendColor
import com.organisation.ui.theme.AppCardSurfaceColorDark
import com.organisation.ui.theme.AppNoneColor
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.AppWeekendColor
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarWidget(
    modifier: Modifier = Modifier,
    month: StateFlow<LocalDate> = MutableStateFlow(LocalDate.now()),
    currentDay: StateFlow<AttendanceRecord>,
    daysMap: StateFlow<Map<LocalDate, AttendanceRecord>> = MutableStateFlow(emptyMap()),
    isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    onRecordClick: (AttendanceRecord) -> Unit,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {}
) {
    val isDarkMode = isSystemInDarkTheme()
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppCardSurfaceColorDark else Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.CALENDAR_WIDGET),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            val loading by isLoading.collectAsStateWithLifecycle()
            if (loading) {
                CalendarSkeleton()
            } else {
                val currentMonthDate by month.collectAsStateWithLifecycle()
                CalenderMonthNavigator(
                    currentMonth = currentMonthDate.monthValue,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
                Spacer(Modifier.height(24.dp))
                CalendarHeader()
                Spacer(Modifier.height(16.dp))
                CalendarGrid(
                    month = month,
                    currentDay = currentDay,
                    daysMap = daysMap,
                    onRecordClick = onRecordClick
                )
            }
        }
    }
}

@Composable
fun CalendarSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.CALENDAR_LOADING_INDICATOR)
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
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
    }
}

@Composable
fun CalenderMonthNavigator(
    modifier: Modifier = Modifier,
    currentMonth: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val monthYearText = remember(currentMonth) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        LocalDate.now().withMonth(currentMonth).format(formatter)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.testTag(AttendanceTestTags.PREVIOUS_MONTH_BUTTON)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.previous_month_label),
                tint = if (isDarkMode) Color.White else AppPrimaryColor
            )
        }

        Text(
            modifier = Modifier.testTag(AttendanceTestTags.CALENDAR_MONTH_TEXT),
            text = monthYearText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else Color.Black
        )

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.testTag(AttendanceTestTags.NEXT_MONTH_BUTTON)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.next_month_label),
                tint = if (isDarkMode) Color.White else AppPrimaryColor
            )
        }
    }
}

@Composable
fun CalendarHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf(
            stringResource(R.string.mon_label),
            stringResource(R.string.tue_label),
            stringResource(R.string.wed_label),
            stringResource(R.string.thu_string),
            stringResource(R.string.fri_label),
            stringResource(R.string.sat_label),
            stringResource(R.string.sun_label)
        ).forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CalendarGrid(
    month: StateFlow<LocalDate>,
    currentDay: StateFlow<AttendanceRecord>,
    modifier: Modifier = Modifier,
    daysMap: StateFlow<Map<LocalDate, AttendanceRecord>>,
    onRecordClick: (AttendanceRecord) -> Unit
) {
    val currentMonthDate by month.collectAsStateWithLifecycle()
    val currentDayRecord by currentDay.collectAsStateWithLifecycle()
    val attendanceMap by daysMap.collectAsStateWithLifecycle()

    val yearMonth = remember(currentMonthDate) { YearMonth.from(currentMonthDate) }
    val daysInMonth = remember(yearMonth) { yearMonth.lengthOfMonth() }

    val paddingDays = remember(currentMonthDate) {
        val firstDayOfMonth = yearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        firstDayOfWeek - 1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.testTag(AttendanceTestTags.CALENDAR_GRID),
        userScrollEnabled = false
    ) {
        repeat(paddingDays) {
            item(key = "padding_$it") { Spacer(Modifier.size(36.dp)) }
        }

        for (day in 1..daysInMonth) {
            item(key = day) {
                val date = yearMonth.atDay(day)
                val record = attendanceMap[date] ?: AttendanceRecord(
                    id = "${currentMonthDate.year}_${currentMonthDate.monthValue}_$day",
                    dayOfMonth = day,
                    dayOfWeek = date.dayOfWeek.name,
                    monthOfYear = currentMonthDate.monthValue,
                    year = currentMonthDate.year,
                    status = AttendanceStatus.None
                )
                val isSelected = date == currentDayRecord.date

                CalendarDayItem(
                    dayOfMonth = day,
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
fun CalendarDayItem(
    dayOfMonth: Int,
    status: AttendanceStatus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val statusColor = status.getColor()

    val backgroundColor = if (isDarkMode) {
        if (status == AttendanceStatus.None || isSelected) {
            AppPrimaryColor
        } else {
            statusColor.copy(alpha = 0.2f)
        }
    } else {
        if (isSelected) AppPrimaryColor
        else if (status == AttendanceStatus.Holiday || status == AttendanceStatus.Weekend || status == AttendanceStatus.PersonalHoliday) AppWeekendColor
        else AppNoneColor
    }

    val textColor = if (isDarkMode) {
        if (status == AttendanceStatus.None) Color(0xFF5C6779) else statusColor
    } else {
        if (isSelected) AppNoneColor
        else if (status == AttendanceStatus.Holiday|| status == AttendanceStatus.Weekend || status == AttendanceStatus.PersonalHoliday) AppCalendarTextWeekendColor
        else if (status == AttendanceStatus.None) Color.Black.copy(alpha = 0.4f)
        else AppCalendarRegularTextColor
    }

    val border = if (isDarkMode && isSelected) {
        BorderStroke(2.dp, AppButtonColorDark)
    } else {
        null
    }

    Box(
        modifier = Modifier
            .testTag(AttendanceTestTags.CALENDAR_DAY_ITEM)
            .aspectRatio(1f)
            .then(
                if (border != null) Modifier.border(
                    border,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayOfMonth.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor
            )
            if (!isDarkMode && status != AttendanceStatus.Holiday && status != AttendanceStatus.None) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(statusColor, CircleShape)
                )
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun CalendarWidgetPreview() {
    val selectedRecord = remember {
        mutableStateOf(dummyRecords.getValue(LocalDate.of(2026, 9, 2)))
    }
    val currentMonth = remember { mutableStateOf(LocalDate.now()) }
    val monthFlow = remember { MutableStateFlow(currentMonth.value) }
    val dayFlow = remember { MutableStateFlow(selectedRecord.value) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }

    OrganisationTheme {
        CalendarWidget(
            month = monthFlow,
            currentDay = dayFlow,
            daysMap = daysMapFlow,
            onRecordClick = {
                selectedRecord.value = it
            },
            onPreviousMonth = {
                currentMonth.value = currentMonth.value.minusMonths(1)
                monthFlow.value = currentMonth.value
            },
            onNextMonth = {
                currentMonth.value = currentMonth.value.plusMonths(1)
                monthFlow.value = currentMonth.value
            }
        )
    }
}

@OrganisationPreviews
@Composable
fun CalendarWidgetLoadingPreview() {
    val monthFlow = remember { MutableStateFlow(LocalDate.now()) }
    val dayFlow = remember {
        MutableStateFlow(
            dummyRecords.getValue(
                LocalDate.of(2026, 9, 2)
            )
        )
    }
    val daysMapFlow = remember { MutableStateFlow(emptyMap<LocalDate, AttendanceRecord>()) }
    val loadingFlow = remember { MutableStateFlow(true) }

    OrganisationTheme {
        CalendarWidget(
            month = monthFlow,
            currentDay = dayFlow,
            daysMap = daysMapFlow,
            isLoading = loadingFlow,
            onRecordClick = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun CalendarWidgetDarkPreview() {
    val selectedRecord = remember {
        mutableStateOf(
            dummyRecords.getValue(
                LocalDate.of(2026, 9, 2)
            )
        )
    }
    val monthFlow = remember { MutableStateFlow(LocalDate.now()) }
    val dayFlow = remember { MutableStateFlow(selectedRecord.value) }
    val daysMapFlow = remember { MutableStateFlow(dummyRecords) }

    OrganisationTheme {
        Box(
            modifier = Modifier
                .background(AppBackgroundDark)
                .padding(16.dp)
        ) {
            CalendarWidget(
                currentDay = dayFlow,
                month = monthFlow,
                daysMap = daysMapFlow,
                onRecordClick = {
                    selectedRecord.value = it
                }
            )
        }
    }
}
