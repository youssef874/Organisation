package com.organisation.data.account

import android.content.Context
import com.organisation.Result
import com.organisation.domaine.account.model.AccountType
import com.organisation.domaine.account.model.AuthError
import com.organisation.domaine.account.model.ConnectionStatus
import com.organisation.domaine.account.repo.AccountRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TestAccountRepo: AccountRepository {

    var notifyNetworkChanges : (suspend (Int)->Unit)? = null
    private var connectionStatus = ConnectionStatus.GOOD_CONNECTION

    var isAuthenticated = false
    var shouldSyncSucceed = true
    var authType = AccountType.UNKNOWN

    private val syncEventFlow = kotlinx.coroutines.flow.MutableSharedFlow<Result<Unit, String>>()

    override fun listenToNetworkChanges(): Flow<@ConnectionStatus Int> {
        return callbackFlow {
            send(ConnectionStatus.GOOD_CONNECTION)
            notifyNetworkChanges = {
                connectionStatus = it
                send(it)
            }
            awaitClose {
                notifyNetworkChanges = null
            }
        }
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return isAuthenticated
    }

    override suspend fun getAuthenticatedAccountType(): @AccountType String {
        return authType
    }

    override suspend fun requestAuthentication(
        context: Context,
        userName: String,
        psw: String
    ): Result<@AccountType String, @AuthError Int> {
        delay(200)
        if (connectionStatus == ConnectionStatus.NO_NETWORK){
            return Result.Error("no network", AuthError.NO_INTERNET_CONNECTION)
        }
        return if (userName == "user" && psw == "psw"){
            Result.Success(AccountType.BOSS)
        }else{
            Result.Error("Unauthorized", AuthError.USER_NOT_AUTHENTICATED)
        }
    }

    override suspend fun syncAccountData() {
        delay(500) // Simulate some work
        if (shouldSyncSucceed) {
            syncEventFlow.emit(Result.Success(Unit))
        } else {
            syncEventFlow.emit(Result.Error("Sync failed", com.organisation.domaine.commun.model.SyncErrorType.UNKNOWN))
        }
    }

    override fun observeAccountSyncEvent(): Flow<Result<Unit, String>> {
        return syncEventFlow
    }
}