package io.pieterswillens.poc.testdoubles

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.EventPublisher

class MockEventPublisher: EventPublisher {
    private val eventHandlers = mutableListOf<EventHandler>()
    private val events = mutableListOf<Event>()

    val lastStoredEvent: Event
        get() = events.last()

    override fun publish(event: Event) {
        events.addLast(event)
        eventHandlers.forEach { it.handle(event) }
    }

    fun subscribe(eventHandler: EventHandler) {
        eventHandlers.add(eventHandler)
    }

    fun hasNoEventPublished(): Boolean {
        return events.isEmpty()
    }
}