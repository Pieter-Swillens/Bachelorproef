package io.pieterswillens.poc.inmemory.eventpublisher

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.dataset.events.DatasetGecreeerd
import io.pieterswillens.poc.domain.dataset.events.SampleOpgeslagen
import io.pieterswillens.poc.domain.email.events.DetectiekenmerkenBerekend
import io.pieterswillens.poc.domain.email.events.EmailGeregistreerd
import io.pieterswillens.poc.domain.risicomodel.events.RisicoModelGetraind
import io.pieterswillens.poc.testdoubles.MockEventHandler
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream

@DisplayName("Given an in-memory event publisher")
class InMemoryEventPublisherTest {

    @Nested
    internal inner class `when the eventpublisher has no registered handlers` {
        val eventPublisher = InMemoryEventPublisher(emptyList())

        @ParameterizedTest
        @ArgumentsSource(EventParams::class)
        fun `then no exception is thrown when the publisher receives an event`(event: Event) {
            assertThatCode { eventPublisher.publish(event) }.doesNotThrowAnyException()
        }
    }

    @Nested
    internal inner class `when the eventpublisher has a registered handler` {
        val aHandler = MockEventHandler()
        val anotherHandler = MockEventHandler()
        val eventPublisher = InMemoryEventPublisher(listOf(aHandler, anotherHandler))

        @ParameterizedTest
        @ArgumentsSource(EventParams::class)
        fun `then the publisher delegates the event to the registered handlers`(event: Event) {
            eventPublisher.publish(event)

            assertThat(aHandler.receivedEvents).containsExactly(event)
            assertThat(anotherHandler.receivedEvents).containsExactly(event)
        }
    }

    @Nested
    internal inner class `when a publisher has a registered handler and receives multiple events`{
        val aHandler = MockEventHandler()
        val eventPublisher = InMemoryEventPublisher(listOf(aHandler))

        @Test
        fun `then all events are delegated to the handler`() {
            eventPublisher.publish(anEvent)
            eventPublisher.publish(anotherEvent)

            assertThat(aHandler.receivedEvents).containsExactly(anEvent, anotherEvent)
        }

        val anEvent = EmailGeregistreerd(emailId = EmailId.createNew(), bestandsnaam = "mail.eml", aard = EmailAard.LEGITIEM)
        val anotherEvent = EmailGeregistreerd(emailId = EmailId.createNew(), bestandsnaam = "anothermail.eml", aard = EmailAard.PHISHING)
    }

    class EventParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(EmailGeregistreerd(emailId = EmailId.createNew(), bestandsnaam = "mail.eml", aard = EmailAard.LEGITIEM)),
                Arguments.of(DetectiekenmerkenBerekend(EmailId.createNew())),
                Arguments.of(SampleOpgeslagen(EmailId.createNew())),
                Arguments.of(DatasetGecreeerd(DatasetId.createNew())),
                Arguments.of(RisicoModelGetraind(RisicoModelId.createNew()))
            )
        }
    }
}