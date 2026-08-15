package com.example.orgcore.internal.account.Authentication.request

import com.example.orgcore.api.account.model.AuthenticationErrorType
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.commun.NetworkResult
import com.example.orgcore.commun.ServerRequest
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
                        Account(
                            id = "1",
                            name = "Admin",
                            phoneNumber = "123456",
                            email = "admin@example.com",
                            image = null,
                            type = AccountUserType.BOSS
                        )
                    )

                    "manager" -> NetworkResult.Success(
                        Account(
                            id = "2",
                            name = "Manager",
                            phoneNumber = "234567",
                            email = "manager@example.com",
                            image = null,
                            type = AccountUserType.MANAGER
                        )
                    )

                    "worker" -> NetworkResult.Success(
                        Account(
                            id = "3",
                            name = "Worker",
                            phoneNumber = "345678",
                            email = "worker@example.com",
                            image = null,
                            type = AccountUserType.WORKER
                        )
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
