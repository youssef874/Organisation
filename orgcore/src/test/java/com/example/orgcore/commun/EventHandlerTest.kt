package com.example.orgcore.commun

import com.example.orgcore.api.model.OrgEvent
import com.example.orgcore.internal.commun.event.EventHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventHandlerTest {

    private class TestEvent(type: String, val data: String = "") : OrgEvent(type)

    private class TestEventHandler : EventHandler()

    @Test
    fun `notify dispatches events to listeners`() {
        val handler = TestEventHandler()
        var receivedEvent: OrgEvent? = null
        val event = TestEvent("type1", "data1")

        handler.addListener(event) {
            receivedEvent = it
        }

        handler.notify(event)

        assertEquals(event, receivedEvent)
    }

    @Test
    fun `removeListener stops dispatching events`() {
        val handler = TestEventHandler()
        var callCount = 0
        val event = TestEvent("type1")
        val listener: (OrgEvent) -> Unit = { callCount++ }

        handler.addListener(event, listener)
        handler.notify(event)
        assertEquals(1, callCount)

        handler.removeListener(event, listener)
        handler.notify(event)
        assertEquals(1, callCount)
    }

    @Test
    fun `observe emits current value immediately`() = runTest {
        val handler = TestEventHandler()
        val event = TestEvent("type1", "initial")
        handler.notify(event)

        val observedEvent = handler.observe("type1").first()

        assertEquals(event, observedEvent)
    }

    @Test
    fun `observe emits future values`() = runTest {
        val handler = TestEventHandler()
        val event1 = TestEvent("type1", "first")
        val event2 = TestEvent("type1", "second")

        val results = mutableListOf<OrgEvent>()
        val job = launch(UnconfinedTestDispatcher()) {
            handler.observe("type1").toList(results)
        }

        handler.notify(event1)
        handler.notify(event2)

        assertEquals(2, results.size)
        assertEquals(event1, results[0])
        assertEquals(event2, results[1])
        job.cancel()
    }

    @Test
    fun `observe is isolated by type`() = runTest {
        val handler = TestEventHandler()
        val event1 = TestEvent("type1")
        val event2 = TestEvent("type2")

        val results1 = mutableListOf<OrgEvent>()
        val job1 = launch(UnconfinedTestDispatcher()) {
            handler.observe("type1").toList(results1)
        }

        val results2 = mutableListOf<OrgEvent>()
        val job2 = launch(UnconfinedTestDispatcher()) {
            handler.observe("type2").toList(results2)
        }

        handler.notify(event1)
        handler.notify(event2)

        assertEquals(1, results1.size)
        assertEquals(event1, results1[0])

        assertEquals(1, results2.size)
        assertEquals(event2, results2[0])

        job1.cancel()
        job2.cancel()
    }

    @Test
    fun `observe registers listener even if no initial value exists`() = runTest {
        val handler = TestEventHandler()
        val event = TestEvent("type1", "data")

        val results = mutableListOf<OrgEvent>()
        val job = launch(UnconfinedTestDispatcher()) {
            handler.observe("type1").toList(results)
        }

        assertTrue(results.isEmpty())

        handler.notify(event)

        assertEquals(1, results.size)
        assertEquals(event, results[0])
        job.cancel()
    }
}
