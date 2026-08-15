package com.example.orgcore.internal.account.request

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.commun.NetworkResult
import com.example.orgcore.commun.ServerRequest
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
        val account = when(
            authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(null)
        ){
            AccountUserType.WORKER-> Account(
                id = "1",
                name = "WORKER",
                email = "worker@gmail.com",
                phoneNumber = "12345678",
                image = null,
                type = AccountUserType.WORKER
            )

            AccountUserType.MANAGER->Account(
                id = "2",
                name = "MANAGER",
                email = "manager@gmail.com",
                phoneNumber = "25836911",
                image = null,
                type = AccountUserType.MANAGER
            )
            else -> Account(
                id = "3",
                name = "BOSS",
                email = "boss@gmail.com",
                phoneNumber = "0000000",
                image = null,
                type = AccountUserType.WORKER
            )
        }
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
