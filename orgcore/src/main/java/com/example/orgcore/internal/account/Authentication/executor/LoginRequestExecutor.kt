package com.example.orgcore.internal.account.Authentication.executor

import com.example.orgcore.api.model.NetworkResult
import com.example.orgcore.internal.account.Authentication.model.LoginRequestConstrains
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.executor.RequestExecutor
import com.example.orgcore.internal.commun.model.RequestResult
import com.example.orgcore.internal.commun.model.ServerRequestErrorType
import com.example.orgcore.internal.commun.request.ServerRequest
import com.example.orgcore.internal.network.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class LoginRequestExecutor(
    private val networkMonitor: NetworkMonitor,
    private val serverRequest: (LoginRequestConstrains)->ServerRequest<Account>,
    private val cache: (LoginRequestConstrains)->OfflineCache<Account>
): RequestExecutor<Account, LoginRequestConstrains> {
    
    companion object{
        private const val CLASS_NAME = "LoginRequestExecutor"
        private const val TAG = "AUTHENTICATION"
    }
    
    override suspend fun execute(constraint: LoginRequestConstrains): RequestResult<Account> {
        LoggerHelper.logger.i(TAG, "${CLASS_NAME}|execute|username: ${constraint.username}")
        return withContext(Dispatchers.IO) {
            require(constraint.username.isNotBlank()) { "Username cannot be blank" }
            require(constraint.password.isNotBlank()) { "Password cannot be blank" }

            if (!networkMonitor.isConnected()) {
                LoggerHelper.logger.w(TAG, "${CLASS_NAME}|execute|Connection lost")
                return@withContext RequestResult.ConnectionLost
            }

            try {
                when (val netResult = serverRequest(constraint).request()) {
                    is NetworkResult.Success -> {
                        val cacheResult = cache(constraint).cache(netResult.data)
                        if (cacheResult) {
                            LoggerHelper.logger.i(TAG, "${CLASS_NAME}|execute|Login success and cached")
                        } else {
                            LoggerHelper.logger.w(TAG, "${CLASS_NAME}|execute|Cache warning: failed to cache login data, proceeding with success")
                        }
                        RequestResult.Success(netResult.data)
                    }
                    is NetworkResult.Error -> {
                        LoggerHelper.logger.e(TAG, "${CLASS_NAME}|execute|Server failure: ${netResult.code}")
                        RequestResult.ServerFailure(netResult.code)
                    }
                }
            } catch (e: Exception) {
                LoggerHelper.logger.e(e,
                    TAG, "${CLASS_NAME}|execute|Error occurred: ${e.message}")
                RequestResult.ServerFailure(ServerRequestErrorType.UNKNOWN_ERROR)
            }
        }
    }
}
