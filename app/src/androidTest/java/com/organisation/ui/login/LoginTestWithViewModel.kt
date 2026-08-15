package com.organisation.ui.login

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.R
import com.organisation.data.account.TestAccountRepo
import com.organisation.domaine.account.model.ConnectionStatus
import com.organisation.ui.login.model.LoginTestTags
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTestWithViewModel {

    private val context = ApplicationProvider.getApplicationContext<Application>()

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun initialState_givenNoNetwork_fieldsAreDisabledAndErrorShown() = runTest {
        val repo = TestAccountRepo()
        rule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(accountRepository = repo, app = context),
                navigateToSplashScreen = {}
            )
        }

        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT).assertDoesNotExist()

        repo.notifyNetworkChanges?.let { it(ConnectionStatus.NO_NETWORK) }


        rule.awaitIdle()

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).assertIsNotEnabled()
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).assertIsNotEnabled()
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).assertIsNotEnabled()

        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT)
            .assertIsDisplayed()
            .assertTextEquals(context.getString(R.string.connection_status))
    }

    @Test
    fun loginClick_givenValidCredentials_showsLoadingThenSucceeds() {
        val repo = TestAccountRepo()
        val viewModel = LoginViewModel(accountRepository = repo, app = context)
        rule.setContent {
            LoginScreen(
                viewModel = viewModel,
                navigateToSplashScreen = {}
            )
        }

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).performTextInput("user")
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).performTextInput("psw")

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON)
            .assertIsEnabled()
            .performClick()

        rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR).assertIsDisplayed()
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).assertDoesNotExist()

        rule.waitUntil(timeoutMillis = 3000) {
            rule.onAllNodesWithTag(LoginTestTags.LOADING_INDICATOR).fetchSemanticsNodes().isEmpty()
        }

        rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR).assertDoesNotExist()
        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT).assertDoesNotExist()

    }

    @Test
    fun loginClick_givenInvalidCredentials_showsError() {
        val repo = TestAccountRepo()
        val viewModel = LoginViewModel(accountRepository = repo, app = context)
        rule.setContent {
            LoginScreen(
                viewModel = viewModel,
                navigateToSplashScreen = {}
            )
        }

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).performTextInput("test")
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).performTextInput("psw")

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON)
            .assertIsEnabled()
            .performClick()

        rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR).assertIsDisplayed()
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).assertDoesNotExist()

        rule.waitUntil(timeoutMillis = 3000) {
            rule.onAllNodesWithTag(LoginTestTags.LOADING_INDICATOR).fetchSemanticsNodes().isEmpty()
        }

        rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR).assertDoesNotExist()
        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT).assertExists()

        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT)
            .assertIsDisplayed()
            .assertTextEquals(context.getString(R.string.error_wrong_user))

    }

    @Test
    fun loginClick_givenNetworkLostDuringRequest_showsConnectionError() = runTest {
        val repo = TestAccountRepo()
        val viewModel = LoginViewModel(accountRepository = repo, app = context)
        rule.setContent {
            LoginScreen(
                viewModel = viewModel,
                navigateToSplashScreen = {}
            )
        }

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).performTextInput("user")
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).performTextInput("psw")

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON)
            .assertIsEnabled()
            .performClick()

        repo.notifyNetworkChanges?.let { it(ConnectionStatus.NO_NETWORK) }

        rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR).assertIsDisplayed()
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).assertDoesNotExist()

        rule.waitUntil(timeoutMillis = 3000) {
            rule.onAllNodesWithTag(LoginTestTags.LOADING_INDICATOR).fetchSemanticsNodes().isEmpty()
        }

        rule.onNodeWithTag(LoginTestTags.LOADING_INDICATOR).assertDoesNotExist()
        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT).assertExists()

        rule.onNodeWithTag(LoginTestTags.CONNECTION_STATE_TEXT)
            .assertIsDisplayed()
            .assertTextEquals(context.getString(R.string.connection_status))

    }

    @Test
    fun reconnection_givenNetworkReturns_fieldsBecomeEnabled() = runTest {
        val repo = TestAccountRepo()
        rule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(accountRepository = repo, app = context),
                navigateToSplashScreen = {})
        }

        repo.notifyNetworkChanges?.invoke(ConnectionStatus.NO_NETWORK)
        rule.awaitIdle()
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).assertIsNotEnabled()

        repo.notifyNetworkChanges?.invoke(ConnectionStatus.GOOD_CONNECTION)
        rule.awaitIdle()

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).assertIsEnabled()
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).assertIsEnabled()
    }

    @Test
    fun loginClick_givenValidCredentials_navigatesToSplash() = runTest {
        val repo = TestAccountRepo()
        var navigationTriggeredCount = 0
        val viewModel = LoginViewModel(accountRepository = repo, app = context)
        rule.setContent {
            LoginScreen(
                viewModel = viewModel,
                navigateToSplashScreen = {
                    navigationTriggeredCount++
                }
            )
        }

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).performTextInput("user")
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).performTextInput("psw")

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).performClick()

        rule.waitUntil(timeoutMillis = 3000) {
            navigationTriggeredCount == 1
        }

        assert(navigationTriggeredCount == 1)
        assert(!viewModel.uiState.value.navigateToSplashScreen)
    }

    @Test
    fun inputValidation_givenEmptyFields_loginButtonIsDisabled() = runTest {
        val repo = TestAccountRepo()
        rule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(accountRepository = repo, app = context),
                navigateToSplashScreen = {})
        }

        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).performTextInput("user")

        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun initialState_givenAlreadyAuthenticated_navigatesToSplash() = runTest {
        val repo = TestAccountRepo().apply {
            isAuthenticated = true
        }
        var navigationTriggeredCount = 0
        rule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(accountRepository = repo, app = context),
                navigateToSplashScreen = {
                    navigationTriggeredCount++
                }
            )
        }

        rule.waitUntil(timeoutMillis = 3000) {
            navigationTriggeredCount == 1
        }

        assert(navigationTriggeredCount == 1)
    }
}
