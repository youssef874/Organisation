package com.example.orgcore.internal.account.sync

import com.example.orgcore.internal.account.AccountSettings
import com.example.orgcore.internal.account.model.Account
import com.example.orgcore.internal.commun.ServerRequest
import com.example.orgcore.internal.commun.cache.OfflineCache
import com.example.orgcore.internal.commun.sync.BaseSynchronizer

/**
 * Concrete [BaseSynchronizer] for synchronizing a list of related accounts.
 *
 * @param relatedUserAccountsServerRequest The request to fetch related accounts.
 * @property relatedUserAccountOfflineCache The cache to store related accounts locally.
 */
internal class RelatedAccountsSynchronizer(
    relatedUserAccountsServerRequest: ServerRequest<List<Account>>,
    private val relatedUserAccountOfflineCache: OfflineCache<List<Account>>,
    private val accountSettings: AccountSettings
) : BaseSynchronizer<List<Account>>(relatedUserAccountsServerRequest) {

    override val className: String = "RelatedAccountsSynchronizer"
    override val tag: String = "ACCOUNT"
    override suspend fun updateIsSynchronized(isSynchronized: Boolean) {
        accountSettings.isRelatedUserAccountSynchronized.updateData(isSynchronized)
    }

    companion object {
        private const val BATCH_SIZE = 200
    }

    override suspend fun performCaching(data: List<Account>): Boolean {
        return data.chunked(BATCH_SIZE).all {
            relatedUserAccountOfflineCache.cache(it)
        }
    }

    override suspend fun isSynchronized(): Boolean {
        return accountSettings.isRelatedUserAccountSynchronized.getData(false)
    }
}
