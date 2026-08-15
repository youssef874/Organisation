package com.organisation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.organisation.OrganisationPreviews
import com.organisation.R
import com.organisation.ui.login.model.ConnectionState
import com.organisation.ui.login.model.LoginErrorState
import com.organisation.ui.login.model.LoginEvent
import com.organisation.ui.login.model.LoginTestTags
import com.organisation.ui.login.model.LoginUiState
import com.organisation.ui.theme.OrganisationTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToSplashScreen:()-> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.navigateToSplashScreen){
        LaunchedEffect(Unit) {
            navigateToSplashScreen()
            viewModel.onEvent(LoginEvent.NavigationHandled)
        }
    }

    LoginScreenContent(
        modifier = modifier,
        uiState = uiState,
        onEmailChanged = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
        onLoginClick = { viewModel.onEvent(LoginEvent.LoginClicked) }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val isConnectionActive = uiState.connectionState == ConnectionState.Connected
    val isInputEnabled = isConnectionActive && !uiState.isLoading
    val isButtonEnabled by remember(uiState) {
        derivedStateOf {
            isInputEnabled && uiState.email.isNotEmpty() && uiState.password.isNotEmpty()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.logo_content_description)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.logo_content_description)
            )
        }

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            label = { Text(stringResource(R.string.email_label)) },
            enabled = isInputEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.EMAIL_TEXT_FIELD),
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(R.string.password_label)) },
            enabled = isInputEnabled,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(LoginTestTags.PASSWORD_TEXT_FIELD),
            leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .testTag(LoginTestTags.LOADING_INDICATOR),
                strokeWidth = 2.dp
            )
        } else {
            Button(
                onClick = onLoginClick,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(LoginTestTags.LOGIN_BUTTON)
            ) {
                Text(
                    stringResource(R.string.button_connect),
                    modifier = Modifier.testTag(LoginTestTags.LOGIN_BUTTON_TEXT)
                )
            }
        }

        if (!isConnectionActive || uiState.errorState != null) {
            val message = if (!isConnectionActive) {
                stringResource(
                    R.string.connection_status,
                    uiState.connectionState.toString()
                )
            }else when (uiState.errorState) {
                LoginErrorState.WrongPassword -> stringResource(R.string.error_wrong_password)
                LoginErrorState.ServerError -> stringResource(R.string.error_server_error)
                LoginErrorState.UnknownError -> stringResource(R.string.error_unknown_error)
                LoginErrorState.Connecting -> stringResource(R.string.status_connecting)
                LoginErrorState.WrongUser->stringResource(R.string.error_wrong_user)
                else -> ""
            }
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .testTag(LoginTestTags.CONNECTION_STATE_TEXT)
                )
            }
        }
    }
}

@OrganisationPreviews
@Composable
fun LoginPreviewAllConfigsContent() {
    OrganisationTheme {
        Surface {
            LoginScreenContent(
                uiState = LoginUiState(connectionState = ConnectionState.Connected),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClick = {}
            )
        }
    }
}

@OrganisationPreviews
@Composable
fun LoginPreviewConnectedContent() {
    OrganisationTheme {
        LoginScreenContent(
            uiState = LoginUiState(connectionState = ConnectionState.Connected),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun LoginPreviewLoadingContent() {
    OrganisationTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "test@example.com",
                password = "password",
                isLoading = true,
                connectionState = ConnectionState.Connected
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun LoginPreviewErrorContent() {
    OrganisationTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "test@example.com",
                password = "wrong",
                connectionState = ConnectionState.Connected,
                errorState = LoginErrorState.WrongPassword
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {}
        )
    }
}

@OrganisationPreviews
@Composable
fun LoginPreviewDisconnectedContent() {
    OrganisationTheme {
        LoginScreenContent(
            uiState = LoginUiState(connectionState = ConnectionState.Disconnected),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {}
        )
    }
}
