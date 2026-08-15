package com.organisation.ui.splash

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.data.account.TestAccountRepo
import com.organisation.domaine.account.model.AccountType
import com.organisation.ui.splash.model.SplashTestTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SplashTestWithViewModel {

    private val context = ApplicationProvider.getApplicationContext<Application>()

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun splashScreen_initialState_is_loading() = runTest {
        val repo = TestAccountRepo()
        rule.setContent {
            SplashScreen(
                viewModel = SplashViewModel(app = context, accountRepository = repo),
            ) {

            }
        }

        rule.onNodeWithTag(SplashTestTags.LOGO).assertIsDisplayed()
        rule.onNodeWithTag(SplashTestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun splashScreen_unauthenticated_navigatesToLogin() = runTest {
        val repo = TestAccountRepo().apply {
            isAuthenticated = false
        }
        var navigationTriggeredCount = 0
        val viewModel = SplashViewModel(app = context, accountRepository = repo)

        val navigationStates = mutableListOf<Boolean>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.map { it.navigateToAuthenticationScreen }.collect {
                navigationStates.add(it)
            }
        }

        rule.setContent {
            SplashScreen(
                viewModel = viewModel,
                navigateToLoginScreen = {
                    navigationTriggeredCount++
                }
            )
        }


        rule.waitUntil(timeoutMillis = 3000) {
            navigationTriggeredCount == 1
        }

        // We expect the state to have been: 
        // 1. false (initial)
        // 2. true (after auth check failed)
        // 3. false (after LaunchedEffect called NavigationHandled)

        assertTrue(
            "Should have reached 'true' state to trigger navigation",
            navigationStates.contains(true)
        )
        assertEquals("Final state should be 'false' after handling", false, navigationStates.last())
        assertEquals("Navigation should be triggered exactly once", 1, navigationTriggeredCount)

        job.cancel()
    }

    @Test
    fun splashScreen_authenticated_syncSuccess_and_account_type_employee_navigateToWorkerHome() = runTest {
        val repo = TestAccountRepo().apply {
            isAuthenticated = true
            authType = AccountType.WORKER
        }
        var navigationTriggeredCount = 0
        val viewModel = SplashViewModel(app = context, accountRepository = repo)

        val navigationStates = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.map { it.navigateToHome }.collect {
                navigationStates.add(it)
            }
        }
        rule.setContent {
            SplashScreen(
                viewModel = viewModel,
                navigateToWorkerHome = {
                    navigationTriggeredCount++
                }

            )
        }
        rule.waitUntil(timeoutMillis = 3000) {
            navigationTriggeredCount == 1
        }

        assertTrue(
            "Should have reached 'employee' state to trigger navigation",
            navigationStates.contains(AccountType.WORKER)
        )
        assertEquals(
            "Final state should be 'unknown' after handling",
            AccountType.UNKNOWN,
            navigationStates.last()
        )
        assertEquals("Navigation should be triggered exactly once", 1, navigationTriggeredCount)

        job.cancel()
    }

    @Test
    fun splashScreen_authenticated_syncSuccess_and_account_type_employee_navigateToManagerHome() = runTest {
        val repo = TestAccountRepo().apply {
            isAuthenticated = true
            authType = AccountType.MANAGER
        }
        var navigationTriggeredCount = 0
        val viewModel = SplashViewModel(app = context, accountRepository = repo)

        val navigationStates = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.map { it.navigateToHome }.collect {
                navigationStates.add(it)
            }
        }
        rule.setContent {
            SplashScreen(
                viewModel = viewModel,
                navigateToManagerHome = {
                    navigationTriggeredCount++
                }

            )
        }
        rule.waitUntil(timeoutMillis = 3000) {
            navigationTriggeredCount == 1
        }

        assertTrue(
            "Should have reached 'manager' state to trigger navigation",
            navigationStates.contains(AccountType.MANAGER)
        )
        assertEquals(
            "Final state should be 'unknown' after handling",
            AccountType.UNKNOWN,
            navigationStates.last()
        )
        assertEquals("Navigation should be triggered exactly once", 1, navigationTriggeredCount)

        job.cancel()
    }

    @Test
    fun splashScreen_authenticated_syncSuccess_and_account_type_employee_navigateToBossHome() = runTest {
        val repo = TestAccountRepo().apply {
            isAuthenticated = true
            authType = AccountType.BOSS
        }
        var navigationTriggeredCount = 0
        val viewModel = SplashViewModel(app = context, accountRepository = repo)

        val navigationStates = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.map { it.navigateToHome }.collect {
                navigationStates.add(it)
            }
        }
        rule.setContent {
            SplashScreen(
                viewModel = viewModel,
                navigateToBossHome = {
                    navigationTriggeredCount++
                }

            )
        }
        rule.waitUntil(timeoutMillis = 3000) {
            navigationTriggeredCount == 1
        }

        assertTrue(
            "Should have reached 'boss' state to trigger navigation",
            navigationStates.contains(AccountType.BOSS)
        )
        assertEquals(
            "Final state should be 'unknown' after handling",
            AccountType.UNKNOWN,
            navigationStates.last()
        )
        assertEquals("Navigation should be triggered exactly once", 1, navigationTriggeredCount)

        job.cancel()
    }

    @Test
    fun splashScreen_syncFailure_showsError() = runTest {
        val repo = TestAccountRepo().apply {
            isAuthenticated = true
            shouldSyncSucceed = false
        }
        rule.setContent {
            SplashScreen(
                viewModel = SplashViewModel(app = context, accountRepository = repo),
            ) {}
        }

        // Initially loading
        rule.onNodeWithTag(SplashTestTags.LOADING_INDICATOR).assertIsDisplayed()

        // Wait for sync to fail
        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodesWithTag(SplashTestTags.LOADING_INDICATOR).fetchSemanticsNodes().isEmpty()
        }

        // Now error should be shown
        rule.onNodeWithTag(SplashTestTags.ERROR_TEXT).assertIsDisplayed()
    }
}
