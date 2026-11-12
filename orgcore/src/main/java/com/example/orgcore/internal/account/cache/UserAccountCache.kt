package com.example.orgcore.internal.account.cache

import com.example.orgcore.internal.account.Authentication.mapper.toDBAccount
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi

/**
 * [OfflineCache] implementation for the current user's account data.
 *
 * @property accountDataApi The API used to interact with the local database.
 */
internal class UserAccountCache(
    private val accountDataApi: AccountDataApi
): OfflineCache<Account> {

    companion object{
        private const val CLASS_NAME = "UserAccountCache"
        private const val TAG = "ACCOUNT"
    }

    override suspend fun cache(data: Account): Boolean {
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|cache|data id : ${data.id}"
        )
        return accountDataApi.addAccountWithDetails(
            data.toDBAccount(
                isUser = true
            )
        )
    }
}