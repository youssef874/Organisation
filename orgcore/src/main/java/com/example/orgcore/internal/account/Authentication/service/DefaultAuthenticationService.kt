package com.example.orgcore.internal.account.Authentication.service

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationError
import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.api.model.Result
import com.example.orgcore.internal.ServicesProvider
import com.example.orgcore.internal.account.Authentication.AuthenticationRequestExecutionBuilderFactory
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.RequestResult
import com.example.orgcore.internal.commun.ServerRequest
import com.example.orgcore.internal.commun.cache.OfflineQueueCache
import com.example.orgcore.internal.network.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DefaultAuthenticationService(
    private val requestFactory: (String, String) -> ServerRequest<Account>,
    private val authenticationProvider: AuthenticationProvider,
    private val networkMonitor: NetworkMonitor = ServicesProvider.getNetworkMonitor(),
    private val offlineQueueCache: OfflineQueueCache,
    private val authBuilderFactory: AuthenticationRequestExecutionBuilderFactory = AuthenticationRequestExecutionBuilderFactory(
        authenticationProvider
    )
) : AuthenticationService {

    companion object {
        private const val CLASS_NAME = "DefaultAuthenticationService"
        private const val TAG = "AUTHENTICATION"
    }

    override suspend fun isAuthenticated(): Boolean {
        return authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData("").isNotBlank()
    }

    override suspend fun getAuthenticatedAccountType(): @AccountUserType Int? {
        val type = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(-1)
        return when (type) {
            AccountUserType.WORKER, AccountUserType.BOSS, AccountUserType.MANAGER -> type
            else -> null
        }
    }

    override suspend fun requestAuthentication(
        userName: String,
        psw: String
    ): Result<Account, AuthenticationError> {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|requestAuthentication| start authentication request"
        )
        return withContext(Dispatchers.IO) {
            if (isAuthenticated()) {
                LoggerHelper.logger.i(TAG, "$CLASS_NAME|requestAuthentication| user already authenticated")
                return@withContext Result.Error(
                    error = AuthenticationError(
                        errorCode = AuthenticationErrorType.ALREADY_AUTHENTICATED,
                        message = "User already authenticated"
                    )
                )
            }

            val request = requestFactory(userName, psw)
            val result = authBuilderFactory.createLogin(request)
                .execute(networkMonitor, offlineQueueCache)

            when (result) {
                is RequestResult.Success -> Result.Success(result.data)
                is RequestResult.ServerFailure -> Result.Error(
                    error = AuthenticationError(
                        errorCode = result.errorCode,
                        message = "Authentication failed"
                    )
                )
                is RequestResult.ConnectionLost -> Result.Error(
                    error = AuthenticationError(
                        errorCode = AuthenticationErrorType.NO_NETWORK,
                        message = "No network connection"
                    )
                )
                is RequestResult.CacheFailure -> Result.Error(
                    error = AuthenticationError(
                        errorCode = AuthenticationErrorType.INTERNAL_SERVER_ERROR,
                        message = "Failed to cache authentication data"
                    )
                )
                is RequestResult.OfflineQueued -> Result.Error(
                    error = AuthenticationError(
                        errorCode = AuthenticationErrorType.UNKNOWN_ERROR,
                        message = "Authentication cannot be queued offline"
                    )
                )
            }
        }
    }
}
