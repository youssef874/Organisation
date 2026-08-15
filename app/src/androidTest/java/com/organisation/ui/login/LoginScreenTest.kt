package com.organisation.ui.login

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.ui.login.model.ConnectionState
import com.organisation.ui.login.model.LoginErrorState
import com.organisation.ui.login.model.LoginTestTags
import com.organisation.ui.login.model.LoginUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val emailField get() = rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD)
    private val passwordField get() = rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD)
    private val loginButton get() = rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON)
    private val loadingIndicator get() = rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR)
    private val connectionStateText get() = rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT)
    private val buttonText get() = rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON_TEXT, useUnmergedTree = true)


    @Test
    fun loginScreen_initialState_whenConnected_isCorrect() {
        rule.setContent {
            LoginScreenContent(
                uiState = LoginUiState(
                    connectionState = ConnectionState.Connected,
                ),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClick = {},
            )
        }
        emailField.assertIsEnabled()
        passwordField.assertIsEnabled()

        connectionStateText.assertDoesNotExist()
        loadingIndicator.assertDoesNotExist()

        loginButton.assertIsNotEnabled()
        buttonText.assertExists()
    }

    @Test
    fun loginScreen_initialState_whenDisconnected_isCorrect() {
        rule.setContent {
            LoginScreenContent(
                uiState = LoginUiState(
                    connectionState = ConnectionState.Disconnected
                ),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClick = {},
            )
        }
        emailField.assertIsNotEnabled()
        passwordField.assertIsNotEnabled()

        connectionStateText.assertExists()
        loadingIndicator.assertDoesNotExist()

        loginButton.assertIsNotEnabled()
        buttonText.assertExists()
    }

    @Test
    fun loginScreen_whenLoading_isCorrect() {
        rule.setContent {
            LoginScreenContent(
                uiState = LoginUiState(
                    isLoading = true,
                    connectionState = ConnectionState.Connected
                ),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClick = {},
            )
        }

        emailField.assertIsNotEnabled()
        passwordField.assertIsNotEnabled()

        loadingIndicator.assertExists()

        loginButton.assertDoesNotExist()
    }

    @Test
    fun loginScreen_whenFieldsAreFilled_buttonIsEnabled() {
        rule.setContent {
            LoginScreenContent(
                uiState = LoginUiState(
                    email = "user@test.com",
                    password = "password123",
                    connectionState = ConnectionState.Connected,
                    isLoading = false
                ),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClick = {},
            )
        }

        loginButton.assertIsEnabled()
    }

    @Test
    fun loginScreen_whenErrorOccurs_displaysErrorMessage() {
        rule.setContent {
            LoginScreenContent(
                uiState = LoginUiState(
                    connectionState = ConnectionState.Connected,
                    errorState = LoginErrorState.WrongPassword
                ),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLoginClick = {},
            )
        }

        connectionStateText.assertExists()
    }
}
