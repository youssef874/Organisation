package com.example.orgcore.internal.account.sync

import com.example.orgcore.commun.BaseSynchronizer
import com.example.orgcore.commun.OfflineCache
import com.example.orgcore.commun.ServerRequest
import com.example.orgcore.internal.account.AccountSettings
import com.example.orgcore.internal.account.model.Account

/**
 * Concrete [BaseSynchronizer] for synchronizing the current user's account data.
 *
 * @param userAccountServerRequest The request to fetch user data.
 * @property userAccountCache The cache to store user data locally.
 */
internal class UserAccountSynchronizer(
    userAccountServerRequest: ServerRequest<Account>,
    private val userAccountCache: OfflineCache<Account>,
    private val accountSettings: AccountSettings
) : BaseSynchronizer<Account>(userAccountServerRequest) {

    override val className: String = "UserAccountSynchronizer"
    override val tag: String = "ACCOUNT_SYNC"

    override suspend fun updateIsSynchronized(isSynchronized: Boolean) {
        accountSettings.isUserAccountSynchronized.updateData(isSynchronized)
    }

    override suspend fun performCaching(data: Account): Boolean {
        println("test $className $data")
        return userAccountCache.cache(data)
    }

    override suspend fun isSynchronized(): Boolean {
        return accountSettings.isUserAccountSynchronized.getData(false) == true
    }
}
