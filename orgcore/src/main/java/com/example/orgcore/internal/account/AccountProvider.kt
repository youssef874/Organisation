package com.example.orgcore.internal.account

import com.example.orgcore.commun.model.OrgEvent
import kotlinx.coroutines.flow.Flow

/**
 * Interface that provides account-related operations and events.
 * This is an internal abstraction used within the account module.
 */
internal interface AccountProvider {

    /**
     * Triggers a fetch/synchronization of account data from the server.
     */
    suspend fun fetchData()

    /**
     * Registers a listener for account-specific events.
     *
     * @param orgEvent The template event type to listen for.
     * @param listener The callback to be invoked.
     */
    fun addAccountEventsListener(
        orgEvent: OrgEvent,
        listener: (OrgEvent)-> Unit
    )

    /**
     * Removes a listener for account-specific events.
     *
     * @param orgEvent The template event type.
     * @param listener The callback to be removed.
     */
    fun removeAccountEventsListener(
        orgEvent: OrgEvent,
        listener: (OrgEvent)-> Unit
    )

    /**
     * Observes account-specific events using a [Flow].
     *
     * @param type The template event type to observe.
     * @return A stream of events matching the type.
     */
    fun observeAccountEventsListener(
        type: String
    ): Flow<OrgEvent>
}