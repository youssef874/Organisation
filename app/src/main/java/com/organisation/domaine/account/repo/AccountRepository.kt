package com.organisation.domaine.account.repo

import android.content.Context
import com.organisation.Result
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.account.model.AuthError
import com.organisation.domaine.account.model.ConnectionStatus

import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    fun listenToNetworkChanges(): Flow<@ConnectionStatus Int>

    suspend fun isUserAuthenticated(): Boolean

    suspend fun getAuthenticatedAccountType(): @AccountType String

    suspend fun requestAuthentication(
        context: Context,
        userName: String,
        psw: String
    ): Result<@AccountType String, @AuthError Int>

    suspend fun syncAccountData()

    fun observeAccountSyncEvent(): Flow<Result<Unit, String>>
}