package com.example.orgcore.internal.account.sync

import com.example.orgcore.internal.account.AccountSettings
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.ServerRequest
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.sync.BaseSynchronizer

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
    override val tag: String = "ACCOUNT"

    override suspend fun updateIsSynchronized(isSynchronized: Boolean) {
        accountSettings.isUserAccountSynchronized.updateData(isSynchronized)
    }

    override suspend fun performCaching(data: Account): Boolean {
        return userAccountCache.cache(data)
    }

    override suspend fun isSynchronized(): Boolean {
        return accountSettings.isUserAccountSynchronized.getData(false) == true
    }
}
