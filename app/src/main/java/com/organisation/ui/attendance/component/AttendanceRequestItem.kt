package com.organisation.ui.attendance.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.common.shimmerEffect
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppCardSurfaceColorDark
import com.organisation.ui.theme.OrganisationTheme
import java.time.LocalDate

@Composable
fun AttendanceRequestItem(
    modifier: Modifier = Modifier,
    record: AttendanceRecord,
    isLoading: Boolean = false,
    showCardBackground: Boolean = true
) {
    val isDarkMode = isSystemInDarkTheme()
    val content = @Composable {
        Row(
            modifier = Modifier
                .padding(if (showCardBackground) 16.dp else 8.dp)
                .fillMaxWidth()
                .then(
                    if (isLoading) Modifier.testTag(AttendanceTestTags.REQUEST_ITEM_LOADING_INDICATOR)
                    else Modifier.testTag(
                        AttendanceTestTags.REQUEST_ITEM
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(140.dp, 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(80.dp, 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp, 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerEffect()
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.requestReason ?: stringResource(R.string.leave_request_label),
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                    val monthName = LocalDate.now().withMonth(record.monthOfYear).month.name
                    Text(
                        text = "$monthName ${record.dayOfMonth}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDarkMode) Color.LightGray else Color.Gray
                    )
                }
                AttendanceStatusBadge(status = record.status)
            }
        }
    }

    if (showCardBackground) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) AppCardSurfaceColorDark else Color.White
            ),
            modifier = modifier.fillMaxWidth()
        ) {
            content()
        }
    } else {
        Box {
            content()
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceRequestItemPreview() {
    OrganisationTheme {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                AttendanceRequestItem(
                    record = AttendanceRecord(
                        id = 1L,
                        dayOfMonth = 10,
                        dayOfWeek = "Monday",
                        status = AttendanceStatus.Present,
                        requestReason = "Vacation"
                    )
                )
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceRequestItemLoadingPreview() {
    OrganisationTheme {
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AttendanceRequestItem(
                    record = AttendanceRecord(
                        id = 1L,
                        dayOfMonth = 10,
                        dayOfWeek = "Monday",
                        status = AttendanceStatus.Present,
                        requestReason = "Vacation"
                    ),
                    isLoading = true
                )
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceRequestItemDarkPreview() {
    OrganisationTheme(darkTheme = true) {
        Surface(color = AppBackgroundDark) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AttendanceRequestItem(
                    record = AttendanceRecord(
                        id = 1L,
                        dayOfMonth = 10,
                        dayOfWeek = "Monday",
                        status = AttendanceStatus.Pending,
                        requestReason = "Vacation"
                    )
                )
            }
        }
    }
}