package io.pieterswillens.poc.testdoubles

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventHandler

class MockEventHandler: EventHandler {
    val receivedEvents: MutableList<Event> = mutableListOf()

    override fun handle(event: Event) {
        receivedEvents.addLast(event)
    }
}