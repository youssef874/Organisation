package com.example.orgcore.internal.account.Authentication.service

import com.example.orgcore.api.account.model.AccountUserType
import com.example.orgcore.api.account.model.AuthenticationError
import com.example.orgcore.api.model.Result
import com.example.orgcore.internal.account.model.Account

/**
 * This abstraction of authentication services
 */
internal interface AuthenticationService {

    /**
     * Call this to check if account authenticated
     *
     * @return true if the user is authenticated otherwise false
     */
    suspend fun isAuthenticated(): Boolean


    /**
     * Call this to get the user account type if is authenticated
     *
     * @return [AccountUserType] if the user is authenticated otherwise it will return null
     */
    suspend fun getAuthenticatedAccountType(): @AccountUserType Int?

    /**
     * Call this to request authentication for user
     *
     * @param userName: the requester user name
     * @param psw: The user password
     *
     * @return [Result.Success] of type [Account] if the user is authorizes to the rest of the sdk services
     * or [Result.Error] of type [com.example.orgcore.api.account.model.AuthenticationError] if authentication failed for some reason
     */
    suspend fun requestAuthentication(userName: String,psw: String): Result<Account, AuthenticationError>
}