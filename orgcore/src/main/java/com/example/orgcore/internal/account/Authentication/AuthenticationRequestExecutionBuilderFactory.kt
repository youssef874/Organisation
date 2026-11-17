package com.example.orgcore.internal.account.Authentication

import com.example.orgcore.internal.account.Authentication.cache.AuthenticationLoginCache
import com.example.orgcore.internal.account.Authentication.cache.AuthenticationLogoutCache
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.RequestExecutionBuilder
import com.example.orgcore.internal.commun.request.ServerRequest

/**
 * Factory for building configured [RequestExecutionBuilder] instances for authentication operations.
 */
internal class AuthenticationRequestExecutionBuilderFactory(
    private val authenticationProvider: AuthenticationProvider
) {

    fun createLogin(
        request: ServerRequest<Account>
    ) = RequestExecutionBuilder(request)
        .allowOfflineQueue(false) // Authentication is online-only
        .cache(AuthenticationLoginCache(authenticationProvider))

    fun createLogout(
        request: ServerRequest<Unit>
    ) = RequestExecutionBuilder(request)
        .allowOfflineQueue(false)
        .cache(AuthenticationLogoutCache(authenticationProvider))
}
