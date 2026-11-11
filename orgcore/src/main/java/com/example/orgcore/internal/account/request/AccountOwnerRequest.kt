package com.example.orgcore.internal.account.request

import com.example.orgcore.commun.FakeData
import com.example.orgcore.commun.ServerRequest
import com.example.orgcore.commun.model.NetworkResult
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.model.Account

/**
 * Fake implementation of [ServerRequest] to fetch the current account owner's profile.
 *
 * @property authenticationProvider Provides the current authentication state to simulate data.
 */
internal class FakeAccountOwnerRequest(
    private val authenticationProvider: AuthenticationProvider,
    var responseCode: Int = 200,
    var shouldThrowException: Boolean = false
): ServerRequest<Account> {


    override suspend fun request(): NetworkResult<Account> {
        if (shouldThrowException) {
            throw RuntimeException("Unexpected server error")
        }

        val accountType = authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider
            .getData(-1)
        val account = FakeData.getFakeData(
            accountType
        ).account
        return if (responseCode == 200){
            NetworkResult.Success(account)
        }else{
            NetworkResult.Error(
                code = responseCode,
                message = "Error"
            )
        }
    }
}
