package com.organisation.ui.splash.model

import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.commun.model.SyncErrorType

data class SplashUiState(
    val isLoading: Boolean = true,
    val isError:  @SyncErrorType String? = null,
    val navigateToAuthenticationScreen: Boolean = false,
    val navigateToHome: @AccountType String = AccountType.UNKNOWN
)

object SplashTestTags {
    const val LOADING_INDICATOR = "splashLoadingIndicator"
    const val ERROR_TEXT = "splashErrorText"
    const val OFFLINE_VIEW = "OfflineView"
    const val LOGO = "splashLogo"
}
