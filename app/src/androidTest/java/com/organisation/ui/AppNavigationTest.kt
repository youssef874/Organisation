package com.organisation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.ui.login.model.LoginTestTags
import com.organisation.ui.splash.model.SplashTestTags
import com.organisation.ui.theme.OrganisationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun navigation_fromSplashToLogin_whenUnauthenticated() {
        // We assume the default state of the repository (or Hilt injection) 
        // results in an unauthenticated user, which triggers splash -> login.
        
        rule.setContent {
            OrganisationTheme {
                LauncherApps()
            }
        }

        // 1. Verify we start at Splash
        rule.onNodeWithTag(SplashTestTags.LOGO).assertIsDisplayed()

        // 2. Wait for Splash to decide to navigate (it checks auth in init)
        // This might take a moment depending on the delay in SplashViewModel
        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodesWithTag(LoginTestTags.EMAIL_TEXT_FIELD).fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Verify Login Screen is displayed
        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).assertIsDisplayed()
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).assertIsDisplayed()
    }

    @Test
    fun navigation_fromLoginToSplash_onSuccess() {
        rule.setContent {
            OrganisationTheme {
                LauncherApps()
            }
        }

        // Wait to arrive at Login Screen
        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodesWithTag(LoginTestTags.EMAIL_TEXT_FIELD).fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Perform Login
        rule.onNodeWithTag(LoginTestTags.EMAIL_TEXT_FIELD).performTextInput("user")
        rule.onNodeWithTag(LoginTestTags.PASSWORD_TEXT_FIELD).performTextInput("psw")
        rule.onNodeWithTag(LoginTestTags.LOGIN_BUTTON).performClick()

        // 2. Wait for navigation back to Splash (or whatever the success destination is)
        // Since the requirement was "navigate to splashScreen" on success:
        rule.waitUntil(timeoutMillis = 5000) {
            rule.onAllNodesWithTag(SplashTestTags.LOGO).fetchSemanticsNodes().isNotEmpty()
        }

        // 3. Verify Splash Screen (or its loading state) is displayed again
        rule.onNodeWithTag(SplashTestTags.LOGO).assertIsDisplayed()
    }
}
