package com.example.orgcore.internal.commun.event

import com.example.orgcore.api.model.OrgEvent
import kotlinx.coroutines.flow.Flow

/**
 * Interface for components that wish to listen to or observe [OrgEvent]s.
 * It supports both callback-based and Flow-based observation.
 */
internal interface OrgEventListener {

    /**
     * Registers a [listener] for a specific [orgEvent] type.
     *
     * @param orgEvent The template event whose type will be used to filter notifications.
     * @param listener The callback function to be invoked when a matching event is notified.
     */
    fun addListener(orgEvent: OrgEvent,listener: (OrgEvent)-> Unit)

    /**
     * Unregisters a previously added [listener] for a specific [orgEvent] type.
     *
     * @param orgEvent The template event used during registration.
     * @param listener The callback function to be removed.
     */
    fun removeListener(orgEvent: OrgEvent,listener: (OrgEvent)-> Unit)

    /**
     * Returns a [Flow] that emits matching [OrgEvent]s as they are notified.
     *
     * @param type The template event whose type will be used to filter notifications.
     * @return A stream of matching events.
     */
    fun observe(type: String): Flow<OrgEvent>
}