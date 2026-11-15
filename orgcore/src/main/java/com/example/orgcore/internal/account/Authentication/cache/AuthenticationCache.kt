package com.example.orgcore.internal.account.Authentication.cache

import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.cache.OfflineCache

/**
 * Cache implementation for persisting authenticated account details into DataStore preferences.
 */
internal class AuthenticationLoginCache(
    private val authenticationProvider: AuthenticationProvider
) : OfflineCache<Account> {
    override suspend fun cache(data: Account): Boolean {
        return try {
            authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.updateData(data.id)
            authenticationProvider.authenticatedUserAccountTypeDataStoreItemProvider.updateData(data.type)
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Cache implementation for clearing authenticated account details from DataStore preferences on logout.
 */
internal class AuthenticationLogoutCache(
    private val authenticationProvider: AuthenticationProvider
) : OfflineCache<Unit> {
    override suspend fun cache(data: Unit): Boolean {
        return try {
            authenticationProvider.clear()
            true
        } catch (e: Exception) {
            false
        }
    }
}
