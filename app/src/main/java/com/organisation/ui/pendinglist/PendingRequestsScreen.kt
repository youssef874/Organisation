package com.organisation.ui.pendinglist

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.organisation.ui.pendinglist.component.AttendanceRequestList
import com.organisation.ui.theme.AppBackground
import com.organisation.ui.theme.AppBackgroundDark
import com.organisation.ui.theme.AppPrimaryColor
import com.organisation.ui.theme.OrganisationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PendingRequestsScreen(
    modifier: Modifier = Modifier,
    isLoading: StateFlow<Boolean>,
    pendingRequests: StateFlow<List<AttendanceRecord>>,
    onBackClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val loading by isLoading.collectAsStateWithLifecycle()
    val requests by pendingRequests.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDarkMode) AppBackgroundDark else AppBackground,
        topBar = {
            PendingRequestsHeader(onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (requests.isEmpty() && !loading) {
                PendingRequestsEmptyState()
            } else {
                AttendanceRequestList(
                    requests = requests,
                    isLoading = loading,
                    useCardStyle = true
                )
            }
        }
    }
}

@Composable
fun PendingRequestsHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppPrimaryColor,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_label),
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.pending_requests_label),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PendingRequestsEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_pending_requests_found_label),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@OrganisationPreviews
@Composable
fun PendingRequestsScreenPreview() {
    val loading = remember { MutableStateFlow(false) }
    val requests = remember {
        MutableStateFlow(dummyRecords.filter { it.value.status == AttendanceStatus.Pending }
            .map { it.value })
    }

    OrganisationTheme {
        PendingRequestsScreen(
            isLoading = loading,
            pendingRequests = requests,
            onBackClick = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun PendingRequestsScreenLoadingPreview() {
    val loading = remember { MutableStateFlow(true) }
    val requests = remember { MutableStateFlow(emptyList<AttendanceRecord>()) }

    OrganisationTheme {
        PendingRequestsScreen(
            isLoading = loading,
            pendingRequests = requests,
            onBackClick = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun PendingRequestsScreenEmptyPreview() {
    val loading = remember { MutableStateFlow(false) }
    val requests = remember { MutableStateFlow(emptyList<AttendanceRecord>()) }

    OrganisationTheme {
        PendingRequestsScreen(
            isLoading = loading,
            pendingRequests = requests,
            onBackClick = {}
        )
    }
}
