package com.organisation.ui.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.organisation.domaine.commun.model.SyncErrorType
import com.organisation.ui.splash.model.SplashTestTags
import com.organisation.ui.splash.model.SplashUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun splashScreen_whenLoading_isCorrect() {
        rule.setContent {
            SplashScreenContent(
                uiState = SplashUiState(isLoading = true)
            )
        }

        rule.onNodeWithTag(SplashTestTags.LOGO).assertIsDisplayed()
        rule.onNodeWithTag(SplashTestTags.LOADING_INDICATOR).assertIsDisplayed()
        rule.onNodeWithTag(SplashTestTags.ERROR_TEXT).assertDoesNotExist()
    }


    @Test
    fun splashScreen_whenError_isCorrect() {
        rule.setContent {
            SplashScreenContent(
                uiState = SplashUiState(isLoading = false, isError = SyncErrorType.UNKNOWN)
            )
        }

        rule.onNodeWithTag(SplashTestTags.LOGO).assertIsDisplayed()
        rule.onNodeWithTag(SplashTestTags.LOADING_INDICATOR).assertDoesNotExist()
        rule.onNodeWithTag(SplashTestTags.ERROR_TEXT).assertIsDisplayed()
    }
}
