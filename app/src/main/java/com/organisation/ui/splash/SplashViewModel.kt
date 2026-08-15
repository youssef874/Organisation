package com.organisation.ui.splash

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.organisation.LoggerHelper
import com.organisation.Result
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.account.repo.AccountRepository
import com.organisation.domaine.commun.model.SyncErrorType
import com.organisation.ui.common.viewmodel.BaseViewModel
import com.organisation.ui.splash.model.SplashEvent
import com.organisation.ui.splash.model.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountRepository: AccountRepository, app: Application
) : BaseViewModel<SplashEvent>(app) {

    companion object {
        private const val CLASS_NAME = "SplashViewModel"
        private const val TAG = "SPLASH"
    }

    override fun onCleared() {
        super.onCleared()
        LoggerHelper.logger.i(
            TAG, "$CLASS_NAME|onCleared"
        )
    }

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuthenticationState()
    }

    private fun checkAuthenticationState() {
        viewModelScope.launch {
            val isUserAuthenticated = accountRepository.isUserAuthenticated()
            LoggerHelper.logger.i(
                TAG,
                "$CLASS_NAME|checkAuthenticationState|checkAuthenticationState|isUserAuthenticated: $isUserAuthenticated"
            )
            _uiState.update {
                it.copy(navigateToAuthenticationScreen = !isUserAuthenticated)
            }
            if (isUserAuthenticated) {
                syncData()
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            launch {
                accountRepository.observeAccountSyncEvent().shareIn(
                    scope = viewModelScope, started = SharingStarted.WhileSubscribed()
                ).collectLatest {
                    LoggerHelper.logger.d(
                        TAG, "$CLASS_NAME|syncData: is synced: $it"
                    )
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isError = (it as? Result.Error<@SyncErrorType String>)?.error,
                            navigateToHome = if (it is Result.Success) accountRepository.getAuthenticatedAccountType()
                            else AccountType.UNKNOWN
                        )
                    }
                    LoggerHelper.logger.d(
                        TAG, "$CLASS_NAME|syncData ${uiState.value}"
                    )
                }
            }
            launch {
                accountRepository.syncAccountData()
            }

        }
    }

    override fun handleEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.AuthNavigationHandled -> {
                LoggerHelper.logger.d(
                    TAG, "$CLASS_NAME|handleEvent|NavigationHandled"
                )
                _uiState.update {
                    it.copy(navigateToAuthenticationScreen = false)
                }
            }

            SplashEvent.HomeNavigationHandled -> {
                LoggerHelper.logger.d(
                    TAG, "$CLASS_NAME|handleEvent|AppNavigationHandled"
                )
                _uiState.update {
                    it.copy(navigateToHome = AccountType.UNKNOWN)
                }
            }
        }
    }
}
