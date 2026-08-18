package com.organisation.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.commun.model.SyncErrorType
import com.organisation.ui.common.components.OrgCircularLoader
import com.organisation.ui.splash.model.SplashEvent
import com.organisation.ui.splash.model.SplashTestTags
import com.organisation.ui.splash.model.SplashUiState
import com.organisation.ui.theme.OrganisationTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToLoginScreen: () -> Unit = {},
    navigateToWorkerHome: () -> Unit = {},
    navigateToManagerHome: () -> Unit= {},
    navigateToBossHome: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.navigateToAuthenticationScreen){
        LaunchedEffect(Unit) {
            navigateToLoginScreen()
            viewModel.onEvent(SplashEvent.AuthNavigationHandled)
        }
    }
    if (uiState.navigateToHome != AccountType.UNKNOWN){
        LaunchedEffect(Unit) {
            when(uiState.navigateToHome){
                AccountType.WORKER-> navigateToWorkerHome()
                AccountType.MANAGER-> navigateToManagerHome()
                AccountType.BOSS-> navigateToBossHome()
            }
            viewModel.onEvent(SplashEvent.HomeNavigationHandled)
        }
    }


    SplashScreenContent(
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
    uiState: SplashUiState
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(150.dp)
                .testTag(SplashTestTags.LOGO),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.logo_content_description),
            )
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.logo_content_description),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            OrgCircularLoader(
                modifier = Modifier.testTag(SplashTestTags.LOADING_INDICATOR)
            )
        }

        if (uiState.isError == SyncErrorType.OFFLINE) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag(SplashTestTags.OFFLINE_VIEW)            ) {
                Icon(Icons.Default.CloudOff, contentDescription = null)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_internet_connection),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(SplashTestTags.ERROR_TEXT)
                )
            }
        }else if (uiState.isError != null){
            Text(
                text = stringResource(R.string.something_went_wrong),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag(SplashTestTags.ERROR_TEXT)
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun SplashPreviewLoadingContent() {
    OrganisationTheme {
        Surface {
            SplashScreenContent(
                uiState = SplashUiState(isLoading = true)
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun SplashPreviewSuccessContent() {
    OrganisationTheme {
        Surface {
            SplashScreenContent(
                uiState = SplashUiState(isLoading = false, navigateToHome = AccountType.WORKER)
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun SplashPreviewErrorContent() {
    OrganisationTheme {
        Surface {
            SplashScreenContent(
                uiState = SplashUiState(isLoading = false, isError = SyncErrorType.OFFLINE)
            )
        }
    }
}
