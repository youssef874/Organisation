package com.organisation.domaine.account.repo

import android.content.Context
import com.example.orgcore.api.account.AccountApi
import com.example.orgcore.api.account.authentication.AuthenticationApi
import com.example.orgcore.api.account.event.AccountEventTypes
import com.example.orgcore.api.account.event.AccountFetchDataResult
import com.example.orgcore.api.account.model.AuthenticatedAccount
import com.example.orgcore.api.account.model.AuthenticationError
import com.example.orgcore.api.model.Result.Error
import com.example.orgcore.api.model.Result.Success
import com.example.orgcore.api.network.NetworkApi
import com.organisation.Result
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.account.model.AuthError
import com.organisation.domaine.account.model.ConnectionStatus
import com.organisation.domaine.commun.model.SyncErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DefaultAccountRepo : AccountRepository {

    override fun listenToNetworkChanges(): Flow<@ConnectionStatus Int> {
        return NetworkApi.listenToNetworkChanges().map { networkConnection ->
            networkConnection.toConnectionStatus()
        }
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return AuthenticationApi.isUserAuthenticated()
    }

    override suspend fun getAuthenticatedAccountType(): @AccountType String {
        return AuthenticationApi.getAuthenticatedAccountType()?.toAccountType()
            ?: AccountType.UNKNOWN
    }

    override suspend fun requestAuthentication(
        context: Context,
        userName: String,
        psw: String
    ): Result<@AccountType String, @AuthError Int> {
        return AuthenticationApi.requestAuthentication(
            userName = userName, psw = psw
        ).let { result ->
            when (result) {
                is Error<AuthenticationError> -> Result.Error(
                    result.error.message, result.error.toAppAuthError()
                )

                is Success<AuthenticatedAccount> -> Result.Success(
                    result.data.accountUserType.toAccountType()
                )
            }
        }
    }

    override suspend fun syncAccountData() {
        AccountApi.fetchData()
    }

    override fun observeAccountSyncEvent(): Flow<Result<Unit, String>> {
        return AccountApi.observeAccountEvents(AccountEventTypes.ACCOUNT_FETCH_RESULT).map {
            if (it is AccountFetchDataResult) {
                it.result.toResult()
            } else {
                Result.Error("unknown", SyncErrorType.UNKNOWN)
            }
        }
    }
}