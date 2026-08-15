package com.example.orgcore.internal.account.Authentication.provider

import com.example.orgcore.internal.FakeDataStoreItemProvider
import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider

internal class FakeAuthenticationProvider : AuthenticationProvider {
    override val authenticatedUserServerIdDataStoreItemProvider: DataStoreItemProvider<String> =  FakeDataStoreItemProvider()
    override val authenticatedUserAccountTypeDataStoreItemProvider = FakeDataStoreItemProvider<Int>()
    override suspend fun clear() {
        authenticatedUserServerIdDataStoreItemProvider.remove()
        authenticatedUserAccountTypeDataStoreItemProvider.remove()
    }
}