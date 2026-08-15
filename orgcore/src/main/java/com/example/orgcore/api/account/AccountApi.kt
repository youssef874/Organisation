package com.example.orgcore.api.account

import com.example.orgcore.SDKComponent
import com.example.orgcore.api.account.event.AccountFetchDataResult
import com.example.orgcore.commun.model.OrgEvent
import com.example.orgcore.internal.ServicesProvider

/**
 * Public API for all account-related operations in the SDK.
 * This is the main entry point for applications to interact with user profiles and related accounts.
 */
object AccountApi {

    /**
     * Triggers a synchronization of account data from the server to the local database.
     * This includes the current user's profile and their related accounts.
     */
    suspend fun fetchData(){
        ServicesProvider.getAccountProvider(
            SDKComponent.SDKContext.getContext()
        ).fetchData()
    }

    /**
     * Observes account-related events using a [kotlinx.coroutines.flow.Flow].
     *
     * @param orgEvent A template event whose type defines which events to observe (e.g., [AccountFetchDataResult]).
     * @return A Flow that emits matching events as they occur.
     */
    fun observeAccountEvents(eventType: String) = ServicesProvider.getAccountProvider(
        SDKComponent.SDKContext.getContext()
    ).observeAccountEventsListener(eventType)
}