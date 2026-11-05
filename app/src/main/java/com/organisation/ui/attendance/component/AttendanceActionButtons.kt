package com.organisation.ui.attendance.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.attendance.model.AttendanceRecord
import com.organisation.domaine.attendance.model.AttendanceStatus
import com.organisation.ui.attendance.model.AttendanceTestTags
import com.organisation.ui.common.components.OrgPrimaryButton
import com.organisation.ui.common.shimmerEffect
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AttendanceActionButtons(
    modifier: Modifier = Modifier,
    record: AttendanceRecord,
    isProcessing: Boolean = false,
    isLoading: Boolean = false,
    isVisible: StateFlow<Boolean> = MutableStateFlow(true),
    onRequestOff: () -> Unit,
    cancelRequest: () -> Unit
) {
    if (isVisible.collectAsStateWithLifecycle().value){
        Column(
            modifier = modifier.testTag(AttendanceTestTags.ACTION_BUTTONS),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .shimmerEffect()
                        .testTag(AttendanceTestTags.ACTION_BUTTONS_LOADING_INDICATOR)
                )
            } else {
                if ( record.status != AttendanceStatus.Pending && record.status != AttendanceStatus.Holiday) {
                    OrgPrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(AttendanceTestTags.REQUEST_OFF_BUTTON),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(16.dp),
                        isButtonEnabled = !isProcessing,
                        onClick = {
                            onRequestOff()
                        }
                    ) {
                        Icon(Icons.Default.Payments, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.request_day_or_partial_off_label))
                    }
                } else if (record.status == AttendanceStatus.Pending) {
                    OrgPrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(AttendanceTestTags.CANCEL_REQUEST_BUTTON),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        isButtonEnabled = !isProcessing,
                        onClick = {
                            cancelRequest()
                        }
                    ) {
                        Text(stringResource(R.string.cancel_request_label))
                    }
                }
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun AttendanceActionButtonsAbsentOrPresentPreview() {
    OrganisationTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                AttendanceActionButtons(
                    record = AttendanceRecord(
                        id = "1",
                        dayOfMonth = 10,
                        dayOfWeek = "Monday",
                        status = AttendanceStatus.Absent
                    ),
                    onRequestOff = {},
                    cancelRequest = {}
                )
            }
        }

    }
}

@OrganisationPreviews
@Composable
fun AttendanceActionButtonsPendingPreview() {
    OrganisationTheme {
        Scaffold { innerPadding ->
            AttendanceActionButtons(
                record = AttendanceRecord(
                    id = "1",
                    dayOfMonth = 10,
                    dayOfWeek = "Monday",
                    status = AttendanceStatus.Pending
                ),
                modifier = Modifier.padding(innerPadding),
                onRequestOff = {},
                cancelRequest = {}
            )
        }

    }
}

@OrganisationPreviews
@Composable
fun AttendanceActionButtonsLoadingPreview() {
    OrganisationTheme {
        Scaffold { innerPadding ->
            AttendanceActionButtons(
                record = AttendanceRecord(
                    id = "1",
                    dayOfMonth = 10,
                    dayOfWeek = "Monday",
                    status = AttendanceStatus.Absent
                ),
                isLoading = true,
                modifier = Modifier.padding(innerPadding),
                onRequestOff = {},
                cancelRequest = {}
            )
        }

    }
}