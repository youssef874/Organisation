package com.example.orgcore.internal.account

import android.content.Context
import com.example.orgcore.commun.OrgEventListener
import com.example.orgcore.commun.model.OrgEvent
import com.example.orgcore.internal.ServicesProvider
import com.example.orgcore.internal.account.service.AccountService
import kotlinx.coroutines.flow.Flow

/**
 * [AccountProvider] implementation that coordinates account service calls and event observation.
 *
 * @property accountService The service used to trigger data synchronization.
 * @property orgEventListener The listener used to manage and observe account events.
 */
internal class AccountManager internal constructor(
    private val accountService: AccountService,
    private val orgEventListener: OrgEventListener = ServicesProvider.getAccountEventsListener()
): AccountProvider {

    companion object{

        private var INSTANCE: AccountProvider? = null

        fun getInstance(context: Context): AccountProvider = INSTANCE?:synchronized(this){
            AccountManager(
                accountService = ServicesProvider.accountService(context)
            )
        }
    }


    override suspend fun fetchData() {
        accountService.requestData()
    }

    override fun addAccountEventsListener(
        orgEvent: OrgEvent,
        listener: (OrgEvent) -> Unit
    ) {
        orgEventListener.addListener(orgEvent, listener)
    }

    override fun removeAccountEventsListener(
        orgEvent: OrgEvent,
        listener: (OrgEvent) -> Unit
    ) {
        orgEventListener.removeListener(orgEvent, listener)
    }

    override fun observeAccountEventsListener(type: String): Flow<OrgEvent> {
        return orgEventListener.observe(type)
    }
}