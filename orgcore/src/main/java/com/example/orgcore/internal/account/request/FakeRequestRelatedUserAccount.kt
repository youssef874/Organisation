package com.example.orgcore.internal.account.request

import com.example.orgcore.commun.FakeData
import com.example.orgcore.commun.ServerRequest
import com.example.orgcore.commun.model.NetworkResult
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.model.Account

/**
 * Fake implementation of [ServerRequest] to fetch a list of related accounts.
 *
 * @property authenticationProvider Provides the current authentication state to simulate data.
 */
internal class FakeRequestRelatedUserAccount(
    private val authenticationProvider: AuthenticationProvider,
    var accountsOverride: List<Account>? = null,
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false
): ServerRequest<List<Account>> {
    override suspend fun request(): NetworkResult<List<Account>> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }
        if (responseCode != 200) {
            return NetworkResult.Error(code = responseCode, message = "Error")
        }
        if (accountsOverride != null) {
            return NetworkResult.Success(accountsOverride!!)
        }
        val accountType = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider
            .getData(-1)
        val accounts = FakeData.getFakeData(accountType).relatedAccount
        return NetworkResult.Success(accounts)
    }
}
