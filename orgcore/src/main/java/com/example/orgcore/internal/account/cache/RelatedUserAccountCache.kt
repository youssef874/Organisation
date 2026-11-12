package com.example.orgcore.internal.account.cache

import com.example.orgcore.internal.account.Authentication.mapper.toDBAccount
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.LoggerHelper
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgdatabase.public.api.AccountDataApi

/**
 * [OfflineCache] implementation for a list of related accounts.
 *
 * @property accountDataApi The API used to interact with the local database.
 */
internal class RelatedUserAccountCache(
    private val accountDataApi: AccountDataApi
) : OfflineCache<List<Account>> {

    companion object {
        private const val CLASS_NAME = "RelatedUserAccountCache"
        private const val TAG = "ACCOUNT_CACHE"
    }

    override suspend fun cache(data: List<Account>): Boolean {
        LoggerHelper.logger.d(
            TAG,
            "$CLASS_NAME|cache|data size: ${data.size}"
        )
        return accountDataApi.addAccountsWithDetails(data.map { it.toDBAccount(false) })
    }
}