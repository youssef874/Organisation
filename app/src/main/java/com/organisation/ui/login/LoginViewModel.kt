package com.organisation.ui.login

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.organisation.LoggerHelper
import com.organisation.Result
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.account.model.AuthError
import com.organisation.domaine.account.model.ConnectionStatus
import com.organisation.domaine.account.repo.AccountRepository
import com.organisation.ui.common.viewmodel.BaseViewModel
import com.organisation.ui.login.model.ConnectionState
import com.organisation.ui.login.model.LoginErrorState
import com.organisation.ui.login.model.LoginEvent
import com.organisation.ui.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository, app: Application
) : BaseViewModel<LoginEvent>(app) {

    companion object {
        private const val CLASS_NAME = "LoginViewModel"
        private const val TAG = "AUTHENTIFICATION"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }

            is LoginEvent.LoginClicked -> {
                performLogin()
            }

            is LoginEvent.NavigationHandled -> {
                _uiState.update { it.copy(navigateToSplashScreen = false) }
            }
        }
    }

    init {
        checkUserAuthentication()
        observeNetworkConnection()
    }

    override fun onCleared() {
        super.onCleared()
        LoggerHelper.logger.i(
            TAG, "$CLASS_NAME|onCleared"
        )
    }
    private fun observeNetworkConnection() {
        viewModelScope.launch {
            accountRepository.listenToNetworkChanges()
                .collectLatest { status ->
                    LoggerHelper.logger.d(
                        TAG, "$CLASS_NAME|observeNetworkConnection|connection status: $status"
                    )
                    val (connectionState, errorState) = when (status) {
                        ConnectionStatus.GOOD_CONNECTION,
                        ConnectionStatus.WEAK_CONNECTION -> ConnectionState.Connected to null

                        ConnectionStatus.NO_NETWORK -> ConnectionState.Disconnected to LoginErrorState.Connecting
                        else -> ConnectionState.Unknown to null
                    }

                    _uiState.update { loginUiState ->
                        loginUiState.copy(
                            connectionState = connectionState,
                            errorState = errorState
                        )
                    }
                }
        }
    }

    private fun checkUserAuthentication() {
        viewModelScope.launch {
            val isAuthenticated =
                accountRepository.isUserAuthenticated()
            LoggerHelper.logger.d(
                TAG, "$CLASS_NAME|checkUserAuthentication|isAuthenticated: $isAuthenticated"
            )
            if (isAuthenticated) {
                _uiState.update {
                    it.copy(navigateToSplashScreen = true)
                }
            }
        }
    }

    private fun @AuthError Int.toLoginErrorState(): LoginErrorState = when (this) {
        AuthError.INTERNAL_SERVER_ERROR -> LoginErrorState.ServerError
        AuthError.USER_NOT_AUTHENTICATED -> LoginErrorState.WrongUser
        AuthError.NO_INTERNET_CONNECTION -> LoginErrorState.Connecting
        else -> LoginErrorState.UnknownError
    }


    fun performLogin() {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|performLogin|request authentication userName: ${uiState.value.email}, psw: ${uiState.value.password}"
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)
            val result = accountRepository.requestAuthentication(
                context = getApplication<Application>().applicationContext,
                userName = uiState.value.email,
                psw = uiState.value.password
            )
            LoggerHelper.logger.i(
                TAG,
                "$CLASS_NAME|performLogin|result: $result"
            )
            _uiState.update { it.copy(isLoading = false) }
            when (result) {
                is Result.Error<@AuthError Int> -> _uiState.update {
                    it.copy(
                        errorState = result.error.toLoginErrorState()
                    )
                }

                is Result.Success<@AccountType String> -> _uiState.update {
                    it.copy(navigateToSplashScreen = true, errorState = null)
                }
            }
        }
    }
}
