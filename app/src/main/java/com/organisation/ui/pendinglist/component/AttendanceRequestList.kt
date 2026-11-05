package com.organisation.ui.pendinglist.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.organisation.OrganisationPreviews
import com.organisation.domaine.attendance.dummyRecords
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.ui.attendance.component.AttendanceRequestItem
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.theme.AppCardSurfaceColorDark
import com.organisation.ui.theme.AppComponentsBorder
import com.organisation.ui.theme.AppComponentsBorderDark
import com.organisation.ui.theme.OrganisationTheme
import java.time.LocalDate

@Composable
fun AttendanceRequestList(
    modifier: Modifier = Modifier,
    requests: List<AttendanceRecord>,
    isLoading: Boolean = false,
    useCardStyle: Boolean = true
) {
    if (useCardStyle) {
        AttendanceRequestLazyList(modifier, isLoading, requests,true)
    } else {
        val isDarkMode = isSystemInDarkTheme()
        Card(
            modifier = modifier.fillMaxWidth()
                .testTag(AttendanceTestTags.ATTENDANCE_PENDING_LIST),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) AppCardSurfaceColorDark else Color.White
            )
        ) {
            AttendanceRequestLazyList(
                modifier = modifier,
                isLoading = isLoading,
                requests = requests,
                useCardStyle = false
            )
        }
    }
}

@Composable
private fun AttendanceRequestLazyList(
    modifier: Modifier,
    isLoading: Boolean,
    requests: List<AttendanceRecord>,
    useCardStyle: Boolean = true
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading) {
            items(5) {
                AttendanceRequestItem(
                    record = dummyRecords.getValue(
                        LocalDate.of(2026, 9, 2)
                    ),
                    isLoading = true,
                    showCardBackground = !useCardStyle
                )
            }
        } else {
            items(requests, key = { it.id }) { request ->
                AttendanceRequestItem(
                    record = request,
                    showCardBackground = useCardStyle
                )
                if (request.id != requests.last().id && !useCardStyle) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = if (isSystemInDarkTheme()) AppComponentsBorderDark else AppComponentsBorder
                    )
                }
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceRequestListCardStylePreview() {
    OrganisationTheme {
        Surface {
            AttendanceRequestList(
                requests = dummyRecords.values.toList(),
                isLoading = false,
                useCardStyle = true
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceRequestListNonCardStylePreview() {
    OrganisationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            AttendanceRequestList(
                requests = dummyRecords.values.toList(),
                isLoading = false,
                useCardStyle = false
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceRequestListLoadingPreview() {
    OrganisationTheme {
        Surface {
            AttendanceRequestList(
                requests = emptyList(),
                isLoading = true,
                useCardStyle = true
            )
        }
    }
}

