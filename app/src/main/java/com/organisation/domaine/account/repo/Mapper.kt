package com.organisation.domaine.account.repo

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationError
import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.api.model.SyncResult
import com.example.orgcore.api.network.data.NetworkConnection
import com.example.orgcore.api.network.data.NetworkStatus
import com.organisation.Result
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.account.model.AuthError
import com.organisation.domaine.account.model.ConnectionStatus
import com.organisation.domaine.commun.model.SyncErrorType

fun @AccountUserType Int.toAccountType() =
    when (this) {
        AccountUserType.BOSS -> AccountType.BOSS
        AccountUserType.MANAGER -> AccountType.MANAGER
        AccountUserType.WORKER -> AccountType.WORKER
        else -> AccountType.UNKNOWN
    }

fun AuthenticationError.toAppAuthError() =
    when (errorCode) {
        AuthenticationErrorType.INTERNAL_SERVER_ERROR,
        AuthenticationErrorType.TIMEOUT -> AuthError.INTERNAL_SERVER_ERROR

        AuthenticationErrorType.NO_NETWORK -> AuthError.NO_INTERNET_CONNECTION
        AuthenticationErrorType.AUTHENTICATION_REQUEST_NOT_VALIDATED -> AuthError.USER_NOT_AUTHENTICATED
        else -> AuthError.UNKNOWN_ERROR
    }

fun NetworkConnection.toConnectionStatus() =
    when (connectionStatus) {
        NetworkStatus.GOOD_CONNECTION -> ConnectionStatus.GOOD_CONNECTION
        NetworkStatus.BAD_CONNECTION -> ConnectionStatus.WEAK_CONNECTION
        else -> ConnectionStatus.NO_NETWORK
    }

fun SyncResult.toResult(): Result<Unit, String>{
    return when(this){
        is SyncResult.Error.NetworkError -> Result.Error("offline", SyncErrorType.OFFLINE)
        SyncResult.Success -> Result.Success(Unit)
        else -> Result.Error(errorMessage = "unknown", SyncErrorType.UNKNOWN)
    }
}