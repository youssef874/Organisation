package com.example.orgcore.internal.account.Authentication.provider

import com.example.orgcore.internal.internal.datastore.DataStoreItemProvider


/**
 * This abstraction represent the authentication data provider
 * This will be the communicator between authentication related information from datastore
 * or database and the rest of the module
 */
internal interface AuthenticationProvider {


    val authenticatedUserServerIdDataStoreItemProvider: DataStoreItemProvider<String>

    val authenticatedUserAccountTypeDataStoreItemProvider: DataStoreItemProvider<Int>

    /**
     * Call this to remove all data related to authentication
     */
    suspend fun clear()
}