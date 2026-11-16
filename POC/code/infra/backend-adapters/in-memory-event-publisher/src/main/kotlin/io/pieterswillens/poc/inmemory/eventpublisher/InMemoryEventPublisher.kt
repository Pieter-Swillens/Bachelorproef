package io.pieterswillens.poc.inmemory.eventpublisher

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.EventPublisher

class InMemoryEventPublisher(
    private val handlers: List<EventHandler>
): EventPublisher {
    override fun publish(event: Event) {
        handlers.forEach { handler -> handler.handle(event) }
    }
}