package io.pieterswillens.poc.testdoubles

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventPublisher

class MockEventPublisher: EventPublisher {
    private val events = mutableListOf<Event>()

    val lastStoredEvent: Event
        get() = events.last()

    override fun publish(event: Event) {
        events.addLast(event)
    }

    fun hasNoEventPublished(): Boolean {
        return events.isEmpty()
    }
}