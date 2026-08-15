package com.organisation.ui.login.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val navigateToSplashScreen: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.Unknown,
    val errorState: LoginErrorState? = null
)

enum class ConnectionState {
    Connected,
    Disconnected,
    Unknown
}

sealed class LoginErrorState {
    object WrongPassword : LoginErrorState()
    object WrongUser : LoginErrorState()
    object ServerError : LoginErrorState()
    object UnknownError : LoginErrorState()
    object Connecting : LoginErrorState()
}

object LoginTestTags{
    const val EMAIL_TEXT_FIELD = "emailTextField"
    const val PASSWORD_TEXT_FIELD = "passwordTextField"
    const val LOGIN_BUTTON = "loginButton"
    const val LOADING_INDICATOR = "loadingIndicator"
    const val CONNECTION_STATE_TEXT = "connectionStateText"
    const val LOGIN_BUTTON_TEXT = "loginButtonText"
}
