package com.example.orgcore.internal.account

import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider
import com.example.orgcore.internal.internal.datastore.DataStoreProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

internal class AccountSettingsImpl(
    private val booleanDataStoreProvider: DataStoreProvider<Boolean>
): AccountSettings {

    companion object{
        private const val IS_USER_ACCOUNT_CACHED = "is_user_account_cached"
        private const val IS_USER_RELATED_ACCOUNT_CACHED = "is_user_related_account_cached"
    }

    override val isUserAccountSynchronized: DataStoreItemProvider<Boolean>
        get() = object : DataStoreItemProvider<Boolean> {
            override suspend fun updateData(data: Boolean) {
                booleanDataStoreProvider.put(IS_USER_ACCOUNT_CACHED,data)
            }

            override fun listenToDataChanges(defaultData: Boolean?): Flow<Boolean?> {
                return booleanDataStoreProvider.observe(IS_USER_ACCOUNT_CACHED,defaultData)
            }

            override suspend fun getData(defaultData: Boolean?): Boolean? {
                return listenToDataChanges(defaultData).firstOrNull()?:defaultData
            }

            override suspend fun remove(): Boolean? {
                return booleanDataStoreProvider.remove(IS_USER_ACCOUNT_CACHED)
            }
        }
    override val isRelatedUserAccountSynchronized: DataStoreItemProvider<Boolean>
        get() = object : DataStoreItemProvider<Boolean> {
            override suspend fun updateData(data: Boolean) {
                booleanDataStoreProvider.put(IS_USER_RELATED_ACCOUNT_CACHED,data)
            }

            override fun listenToDataChanges(defaultData: Boolean?): Flow<Boolean?> {
                return booleanDataStoreProvider.observe(IS_USER_RELATED_ACCOUNT_CACHED,defaultData)
            }

            override suspend fun getData(defaultData: Boolean?): Boolean? {
                return listenToDataChanges(defaultData).firstOrNull()?:defaultData
            }

            override suspend fun remove(): Boolean? {
               return booleanDataStoreProvider.remove(IS_USER_RELATED_ACCOUNT_CACHED)
            }
        }
}