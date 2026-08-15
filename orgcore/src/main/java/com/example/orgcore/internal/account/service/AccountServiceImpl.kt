package com.example.orgcore.internal.account.service

import android.content.Context
import com.example.orgcore.api.account.event.AccountFetchDataResult
import com.example.orgcore.commun.AccountCompositeSynchronizer
import com.example.orgcore.commun.LoggerHelper
import com.example.orgcore.commun.OrgEventNotifier
import com.example.orgcore.commun.SyncResult
import com.example.orgcore.commun.Synchronizer
import com.example.orgcore.internal.ServicesProvider
import com.example.orgcore.internal.account.Authentication.provider.AuthenticationProvider
import com.example.orgcore.internal.account.cache.RelatedUserAccountCache
import com.example.orgcore.internal.account.cache.UserAccountCache
import com.example.orgcore.internal.account.request.FakeAccountOwnerRequest
import com.example.orgcore.internal.account.request.FakeRequestRelatedUserAccount
import com.example.orgcore.internal.account.sync.RelatedAccountsSynchronizer
import com.example.orgcore.internal.account.sync.UserAccountSynchronizer
import com.example.orgcore.internal.network.NetworkMonitor
import com.example.orgdatabase.public.api.AccountDataApi

internal class AccountServiceImpl internal constructor(
    private val accountSynchronizer: Synchronizer,
    private val networkMonitor: NetworkMonitor,
    private val accountEventNotifier: OrgEventNotifier,
    private val authenticationProvider: AuthenticationProvider
): AccountService {

    companion object{
        private const val CLASS_NAME = "AccountServiceImpl"
        private const val TAG = "ACCOUNT"

        private var INSTANCE: AccountService? = null

        fun getInstance(context: Context): AccountService = INSTANCE?:synchronized(this){
            AccountServiceImpl(
                accountSynchronizer = createSynchronizer(context),
                networkMonitor = ServicesProvider.getNetworkMonitor(),
                accountEventNotifier = ServicesProvider.getAccountEventsDispatcher(),
                authenticationProvider = ServicesProvider.getAuthenticationProvider(context)
            )
        }

        internal fun createSynchronizer(context: Context): Synchronizer {
            val authProvider = ServicesProvider.getAuthenticationProvider(context)
            val accountDataApi = AccountDataApi(context)
            val accountSettings = ServicesProvider.getAccountSettings(context = context)

            val userSync = UserAccountSynchronizer(
                userAccountServerRequest = FakeAccountOwnerRequest(authProvider),
                userAccountCache = UserAccountCache(accountDataApi),
                accountSettings = accountSettings
            )

            val relatedSync = RelatedAccountsSynchronizer(
                relatedUserAccountsServerRequest = FakeRequestRelatedUserAccount(authProvider),
                relatedUserAccountOfflineCache = RelatedUserAccountCache(accountDataApi),
                accountSettings = accountSettings
            )

            return AccountCompositeSynchronizer(listOf(userSync, relatedSync))
        }
    }

    override suspend fun requestData() {
        if (accountSynchronizer.isSynchronized()){
            LoggerHelper.logger.i(
                TAG,
                "$CLASS_NAME|requestData|Already synchronized"
            )
            println("test requestData: Already synchronized")
            accountEventNotifier.notify(
                AccountFetchDataResult(
                    result = SyncResult.Success
                )
            )
            return
        }
        if (!networkMonitor.isConnected()){
            LoggerHelper.logger.e(
                TAG,
                "$CLASS_NAME|requestData| not connected"
            )
            accountEventNotifier.notify(
                AccountFetchDataResult(
                    result = SyncResult.Error.NetworkError()
                )
            )
            return
        }
        if (authenticationProvider.authenticatedUserServerIdDataStoreItemProvider.getData(null) == null){
            LoggerHelper.logger.e(
                TAG,
                "$CLASS_NAME|requestData| User not authenticated could not sync account data"
            )
            accountEventNotifier.notify(
                AccountFetchDataResult(
                    result = SyncResult.Error.Unauthorized()
                )
            )
            return
        }
        val result = accountSynchronizer.synchronize()
        println("test requestData: $result")
        accountEventNotifier.notify(
            AccountFetchDataResult(
                result = result
            )
        )
        LoggerHelper.logger.i(
            TAG,
            "$CLASS_NAME|requestData| result: $result"
        )
    }
}
