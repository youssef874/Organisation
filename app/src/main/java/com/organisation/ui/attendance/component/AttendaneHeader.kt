package com.organisation.ui.attendance.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.domaine.attendance.sampleStats
import com.organisation.ui.attendance.getLabelAndColor
import com.organisation.ui.attendance.model.AttendanceStat
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.common.shimmerEffect
import com.organisation.ui.theme.AppCardSurfaceColorDark
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AttendanceHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.attendance),
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AttendanceStatCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isCompact: Boolean = false,
    stat: AttendanceStat,
    onStatCardClicked: (AttendanceStatus) -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    Card(
        modifier = modifier
            .testTag(AttendanceTestTags.STAT_CARD)
            .clickable(enabled = !isLoading) { onStatCardClicked(stat.attendanceStatus) },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isDarkMode) AppCardSurfaceColorDark else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = if (isCompact) 4.dp else 14.dp,
                    horizontal = if (isCompact) 2.dp else 6.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(AttendanceTestTags.STATS_LOADING_INDICATOR),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 20.dp else 32.dp, if (isCompact) 14.dp else 24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(Modifier.height(if (isCompact) 2.dp else 8.dp))
                    Box(
                        modifier = Modifier
                            .size(if (isCompact) 28.dp else 48.dp, if (isCompact) 6.dp else 10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .shimmerEffect()
                    )
                }
            } else {
                Text(
                    text = stat.value,
                    color = stat.valueColor,
                    fontSize = if (isCompact) 16.sp else 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stat.label,
                    fontSize = if (isCompact) 7.sp else 9.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AttendanceStatsRow(
    modifier: Modifier = Modifier,
    isLoading: StateFlow<Boolean> ,
    isCompact: Boolean = false,
    stats: StateFlow<List<AttendanceStat>>,
    onStatCardClicked: (AttendanceStatus) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.ATTENDANCE_ROW),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.collectAsStateWithLifecycle().value.forEach { stat ->
            AttendanceStatCard(
                stat = stat.getLabelAndColor(),
                modifier = Modifier.weight(1f),
                isLoading = isLoading.collectAsStateWithLifecycle().value,
                isCompact = isCompact,
                onStatCardClicked = onStatCardClicked
            )
        }
    }
}

@Composable
fun AttendanceHeaderWithStats(
    modifier: Modifier = Modifier,
    isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    stats: StateFlow<List<AttendanceStat>>,
    onStatCardClicked: (AttendanceStatus) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.ATTENDANCE_HEADER)
            .background(
                color = AppPrimaryColor
            )
            .padding(start = 20.dp, end = 20.dp, bottom = 18.dp)
            .statusBarsPadding()
    ) {
        AttendanceHeader()
        Spacer(Modifier.height(14.dp))
        AttendanceStatsRow(
            stats = stats,
            isLoading = isLoading,
            onStatCardClicked = onStatCardClicked
        )
    }
}

@Composable
fun HorizontalAttendanceHeaderWithStats(
    modifier: Modifier = Modifier,
    isLoading: StateFlow<Boolean> = MutableStateFlow(false),
    stats: StateFlow<List<AttendanceStat>>,
    onStatCardClicked: (AttendanceStatus) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(AttendanceTestTags.ATTENDANCE_HEADER)
            .background(color = AppPrimaryColor)
            .padding(vertical = 4.dp, horizontal = 20.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AttendanceHeader()
        Spacer(Modifier.width(16.dp))
        AttendanceStatsRow(
            modifier = Modifier.weight(1f),
            stats = stats,
            isLoading = isLoading,
            isCompact = true,
            onStatCardClicked = onStatCardClicked
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OrganisationPreviews
@Composable
fun AttendanceHeaderWithStatsPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    OrganisationTheme {
        Scaffold { _ ->
            AttendanceHeaderWithStats(
                stats = stats,
                onStatCardClicked = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OrganisationPreviews
@Composable
fun HorizontalAttendanceHeaderWithStatsPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    OrganisationTheme {
        Scaffold { _ ->
            HorizontalAttendanceHeaderWithStats(
                stats = stats,
                onStatCardClicked = {}
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OrganisationPreviews
@Composable
fun HorizontalAttendanceHeaderLoadingPreview() {
    val stats = remember { MutableStateFlow(sampleStats) }
    val isLoading = remember { MutableStateFlow(true) }
    OrganisationTheme {
        Scaffold { _ ->
            HorizontalAttendanceHeaderWithStats(
                stats = stats,
                isLoading = isLoading,
                onStatCardClicked = {}
            )
        }
    }
}
