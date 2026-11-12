package com.example.orgcore.internal.commun.event

import com.example.orgcore.api.model.OrgEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * Abstract implementation of [OrgEventNotifier] and [OrgEventListener].
 * It manages a set of listeners and the last emitted values for each event type.
 */
internal abstract class EventHandler: OrgEventNotifier, OrgEventListener {

    private val map = ConcurrentHashMap<String, List<(OrgEvent)-> Unit>>()
    private val values = ConcurrentHashMap<String, OrgEvent>()

    override fun notify(event: OrgEvent) {
        values[event.type] = event
        map[event.type]?.forEach { it(event) }
    }

    override fun addListener(
        orgEvent: OrgEvent,
        listener: (OrgEvent) -> Unit
    ) {
        map[orgEvent.type] = map[orgEvent.type]?.plus(listener) ?: listOf(listener)
    }

    override fun removeListener(orgEvent: OrgEvent, listener: (OrgEvent) -> Unit) {
        map[orgEvent.type] = map[orgEvent.type]?.minus(listener) ?: listOf()
    }

    override fun observe(type: String): Flow<OrgEvent> = callbackFlow {
        values[type]?.let {
            trySend(it)
        }
        val callback: (OrgEvent) -> Unit = { event: OrgEvent ->
            trySend(event)
        }
        val templateEvent = object : OrgEvent(type) {}
        addListener(templateEvent, callback)
        awaitClose {
            removeListener(templateEvent, callback)
        }
    }
}