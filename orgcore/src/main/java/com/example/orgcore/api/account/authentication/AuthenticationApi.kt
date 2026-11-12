package com.example.orgcore.api.account.authentication

import com.example.orgcore.SDKComponent
import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticatedAccount
import com.example.orgcore.api.account.model.AuthenticationError
import com.example.orgcore.api.model.Result
import com.example.orgcore.internal.ServicesProvider
import com.example.orgcore.internal.account.Authentication.mapper.toApiAuthenticatedAccount

object AuthenticationApi {

    /**
     * Call this to check if account authenticated
     *
     * @return true if the user is authenticated otherwise false
     */
    suspend fun isUserAuthenticated() = ServicesProvider.getAuthenticationService(SDKComponent.SDKContext.getContext()).isAuthenticated()

    /**
     * Call this to get the user account type if is authenticated
     *
     * @return [AccountUserType] if the user is authenticated otherwise it will return null
     */
    suspend fun getAuthenticatedAccountType() = ServicesProvider.getAuthenticationService(SDKComponent.SDKContext.getContext()).getAuthenticatedAccountType()

    /**
     * Call this to request authentication for user
     *
     * @param userName: the requester userName
     * @param psw: The user password
     *
     * @return [Result.Success] of type [AuthenticatedAccount] if the user is authorizing to the rest of the sdk services
     * or [Result.Error] of type [AuthenticationError] if authentication failed for some reason
     */
    suspend fun requestAuthentication(userName: String,psw: String): Result<AuthenticatedAccount, AuthenticationError> =
        ServicesProvider.getAuthenticationService(SDKComponent.SDKContext.getContext())
            .requestAuthentication(userName, psw).let {
            when(it) {
                is Result.Error -> it
                is Result.Success -> Result.Success(it.data.toApiAuthenticatedAccount())
            }
        }
}