package io.pieterswillens.poc.use.cases.event.handlers

import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.domain.email.events.DetectiekenmerkenBerekend
import io.pieterswillens.poc.domain.email.events.EmailGeregistreerd
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.email.MockDetectiekenmerken
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.testbuilders.GeregistreerdeEmailTestBuilder
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given an event handler for email events")
class EmailEventHandlerTest {

    private val emailRepository = MockEmailRepository()
    private val eventPublisher = MockEventPublisher()
    private val handler = EmailEventHandler(
        BerekenDetectiekenmerkenUseCase(
            detectiekenmerken = MockDetectiekenmerken(),
            emailRepository = emailRepository,
            eventPublisher = eventPublisher,
        )
    )

    @Nested
    internal inner class `when an EmailGeregistreerd event is handled for a stored email` {
        private val emailId = EmailId.createNew()

        @BeforeEach
        fun setUp() {
            emailRepository.save(GeregistreerdeEmailTestBuilder().with(emailId = emailId).build())

            handler.handle(EmailGeregistreerd(emailId = emailId, bestandsnaam = "mail.eml", aard = EmailAard.LEGITIEM,))
        }

        @Test
        fun `then the detectiekenmerken are berekend`() {
            assertThat(emailRepository.get(emailId)!!.status)
                .isInstanceOf(EmailStatus.DetectiekenmerkenBerekend::class.java)
        }

        @Test
        fun `then a DetectiekenmerkenBerekend event is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(DetectiekenmerkenBerekend(emailId))
        }
    }
}