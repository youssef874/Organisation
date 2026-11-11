package com.example.orgcore.internal.account.Authentication.request

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.commun.FakeData
import com.example.orgcore.commun.ServerRequest
import com.example.orgcore.commun.model.NetworkResult
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.network.NetworkMonitor
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds

internal class FakeAuthenticationRequest(
    private val networkMonitor: NetworkMonitor,
    private val userName: String,
    private val psw: String
) : ServerRequest<Account> {

    companion object {
        private const val REQUEST_TIMEOUT = 5000L
    }

    override suspend fun request(): NetworkResult<Account> {
        if (!networkMonitor.isConnected()) {
            return NetworkResult.Error(
                code = AuthenticationErrorType.NO_NETWORK,
                message = "not connected"
            )
        }

        return try {
            withTimeout(REQUEST_TIMEOUT.milliseconds) {
                if (userName == "timeout") {
                    delay(10000.milliseconds)
                }

                when (userName) {
                    "admin" -> NetworkResult.Success(
                        FakeData.getFakeData(
                            AccountUserType.BOSS
                        ).account
                    )

                    "manager" -> NetworkResult.Success(
                        FakeData.getFakeData(
                            AccountUserType.MANAGER
                        ).account
                    )

                    "worker" -> NetworkResult.Success(
                        FakeData.getFakeData(
                            AccountUserType.WORKER
                        ).account
                    )

                    else -> NetworkResult.Error(
                        code = AuthenticationErrorType.AUTHENTICATION_REQUEST_NOT_VALIDATED,
                        message = "Invalid credentials"
                    )
                }
            }
        } catch (e: TimeoutCancellationException) {
            NetworkResult.Error(
                code = AuthenticationErrorType.TIMEOUT,
                message = "Request timed out",
                exception = e
            )
        }
    }
}
