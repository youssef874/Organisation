package com.example.orgcore.internal.account.Authentication.service

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationError
import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.commun.LoggerHelper
import com.example.orgcore.commun.NetworkResult
import com.example.orgcore.commun.ServerRequest
import com.example.orgcore.commun.model.Result
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


internal class DefaultAuthenticationService(
    private val requestFactory: (String, String) -> ServerRequest<Account>,
    private val authenticationProvider: AuthenticationProvider,
) : AuthenticationService {

    companion object {
        private const val CLASS_NAME = "DefaultAuthenticationService"
        private const val TAG = "AUTHENTICATION"
    }

    override suspend fun isAuthenticated(): Boolean {
        return authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData(null) != null
    }

    override suspend fun getAuthenticatedAccountType(): @AccountUserType Int? {
        return authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(null)
            ?.let { type ->
                when (type) {
                    AccountUserType.WORKER, AccountUserType.BOSS, AccountUserType.MANAGER -> type
                    else -> null
                }
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
            val networkResult = request.request()
            
            LoggerHelper.logger.i(TAG, "$CLASS_NAME|requestAuthentication| result: $networkResult")
            
            when (networkResult) {
                is NetworkResult.Success -> {
                    authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.updateData(networkResult.data.id)
                    authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(networkResult.data.type)
                    Result.Success(networkResult.data)
                }
                is NetworkResult.Error -> {
                    Result.Error(
                        throwable = networkResult.exception,
                        error = AuthenticationError(
                            errorCode = networkResult.code,
                            message = networkResult.message
                        )
                    )
                }
            }
        }
    }
}
