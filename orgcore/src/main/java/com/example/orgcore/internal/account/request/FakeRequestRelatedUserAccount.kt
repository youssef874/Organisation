package com.example.orgcore.internal.account.request

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.commun.NetworkResult
import com.example.orgcore.commun.ServerRequest
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
        val worker1 = Account(
            id = "1",
            name = "WORKER",
            email = "worker@gmail.com",
            phoneNumber = "12345678",
            image = null,
            type = AccountUserType.WORKER
        )

        val worker2 = Account(
            id = "4",
            name = "WORKER2",
            email = "worker2@gmail.com",
            phoneNumber = "12345670",
            image = null,
            type = AccountUserType.WORKER
        )

        val worker3 = Account(
            id = "5",
            name = "WORKER3",
            email = "worker3@gmail.com",
            phoneNumber = "12345671",
            image = null,
            type = AccountUserType.WORKER
        )

        val manager = Account(
            id = "2",
            name = "MANAGER",
            email = "manager@gmail.com",
            phoneNumber = "25836911",
            image = null,
            type = AccountUserType.MANAGER
        )

        val boss = Account(
        id = "3",
        name = "BOSS",
        email = "boss@gmail.com",
        phoneNumber = "0000000",
        image = null,
        type = AccountUserType.WORKER
        )

        val accounts = when(
            authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.getData(null)
        ){
            AccountUserType.WORKER->listOf(
                worker1,manager,boss
            )
            AccountUserType.MANAGER-> listOf(
                worker1,worker2,boss
            )
            else -> listOf(
                worker1,worker2,worker3,boss,manager
            )
        }
        return NetworkResult.Success(accounts)
    }
}
