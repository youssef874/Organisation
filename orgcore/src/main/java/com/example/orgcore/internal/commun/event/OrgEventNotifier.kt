package com.example.orgcore.internal.commun.event

import com.example.orgcore.api.model.OrgEvent

/**
 * Interface for components that dispatch [OrgEvent]s to listeners.
 */
internal interface OrgEventNotifier {

    /**
     * Notifies all registered listeners about the provided [event].
     *
     * @param event The event to be dispatched.
     */
    fun notify(event: OrgEvent)
}