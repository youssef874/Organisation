package com.organisation.ui.attendance.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.organisation.OrganisationPreviews
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.common.getColor
import com.organisation.ui.common.getStringResId
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.OrganisationTheme

@Composable
fun AttendanceStatusBadge(modifier: Modifier = Modifier, status: AttendanceStatus) {
    val isDarkMode = isSystemInDarkTheme()
    val color = status.getColor()
    Surface(
        modifier = modifier.testTag(AttendanceTestTags.ATTENDANCE_STATUS_BADGE),
        color = color.copy(alpha = if (isDarkMode) 0.2f else 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = stringResource(status.getStringResId()),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@OrganisationPreviews
@Composable
fun AttendanceStatusBadgePresencePreview() {
    OrganisationTheme {
        AttendanceStatusBadge(status = AttendanceStatus.Present)
    }
}

@OrganisationPreviews
@Composable
fun AttendanceAttendanceStatusBadgeAbsencePreview() {
    OrganisationTheme {
        AttendanceStatusBadge(status = AttendanceStatus.Absent)
    }
}

@OrganisationPreviews
@Composable
fun AttendanceStatusBadgeDarkPreview() {
    OrganisationTheme(darkTheme = true) {
        Surface(color = AppBackgroundDark) {
            AttendanceStatusBadge(
                status = AttendanceStatus.Present,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
