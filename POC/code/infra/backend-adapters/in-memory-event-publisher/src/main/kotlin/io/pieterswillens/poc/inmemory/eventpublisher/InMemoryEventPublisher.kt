package io.pieterswillens.poc.inmemory.eventpublisher

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.EventPublisher
import org.slf4j.LoggerFactory

class InMemoryEventPublisher(
    private val handlers: List<EventHandler>
): EventPublisher {
    override fun publish(event: Event) {
        LOGGER.info("Publishing event: {}", event)
        handlers.forEach { handler -> handler.handle(event) }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(InMemoryEventPublisher::class.java)
    }
}