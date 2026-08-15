package com.example.orgcore.internal.account.Authentication.provider

import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider
import com.example.orgcore.internal.internal.datastore.DataStoreProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull


internal class AuthenticationProviderDefault(
    private val stringDataStoreProvider: DataStoreProvider<String>,
    private val intDataStoreProvider: DataStoreProvider<Int>
): AuthenticationProvider {

    companion object{
        internal const val AUTHENTICATED_USER_SERVER_SERVER_ID =  "authenticatedUserServerId"
        internal const val AUTHENTICATED_USER_ACCOUNT_TYPE = "authenticatedUserAccountType"
    }

    override val authenticatedUserServerIdDataStoreItemProvider: DataStoreItemProvider<String> =
        object : DataStoreItemProvider<String> {
            override suspend fun updateData(data: String) {
                stringDataStoreProvider.put(AUTHENTICATED_USER_SERVER_SERVER_ID, data)
            }

            override fun listenToDataChanges(defaultData: String?): Flow<String?> {
                return stringDataStoreProvider.observe(AUTHENTICATED_USER_SERVER_SERVER_ID, defaultData)
            }

            override suspend fun getData(defaultData: String?): String? {
                return stringDataStoreProvider.observe(AUTHENTICATED_USER_SERVER_SERVER_ID, defaultData)
                    .firstOrNull()?:defaultData
            }

            override suspend fun remove(): String? {
                return stringDataStoreProvider.remove(AUTHENTICATED_USER_SERVER_SERVER_ID)
            }

        }

    override val authenticatedUserAccountTypeDataStoreItemProvider: DataStoreItemProvider<Int> = object : DataStoreItemProvider<Int> {
        override suspend fun updateData(data: Int) {
            intDataStoreProvider.put(AUTHENTICATED_USER_ACCOUNT_TYPE, data)
        }

        override fun listenToDataChanges(defaultData: Int?): Flow<Int?> {
            return intDataStoreProvider.observe(AUTHENTICATED_USER_ACCOUNT_TYPE, defaultData)
        }

        override suspend fun getData(defaultData: Int?): Int? {
            return intDataStoreProvider.observe(AUTHENTICATED_USER_ACCOUNT_TYPE, defaultData)
                .firstOrNull()?:defaultData
        }

        override suspend fun remove(): Int? {
            return intDataStoreProvider.remove(AUTHENTICATED_USER_ACCOUNT_TYPE)
        }

    }

    override suspend fun clear() {
        authenticatedUserServerIdDataStoreItemProvider.remove()
        authenticatedUserAccountTypeDataStoreItemProvider.remove()
    }
}