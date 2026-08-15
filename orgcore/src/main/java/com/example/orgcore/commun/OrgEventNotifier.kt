package com.example.orgcore.commun

import com.example.orgcore.commun.model.OrgEvent

/**
 * Interface for components that dispatch [OrgEvent]s to listeners.
 */
interface OrgEventNotifier {

    /**
     * Notifies all registered listeners about the provided [event].
     *
     * @param event The event to be dispatched.
     */
    fun notify(event: OrgEvent)
}