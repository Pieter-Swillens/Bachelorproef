package io.pieterswillens.poc.use.cases.email

import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailSnapshot
import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.domain.email.events.EmailGeregistreerd
import io.pieterswillens.poc.domain.email.ports.ParsedEmail
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.StubbedEmailParser
import io.pieterswillens.poc.testdoubles.email.testbuilders.RawEmailTestBuilder
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId
import io.pieterswillens.poc.vocabulary.RawEmail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a use case to registreer an email")
class RegistreerEmailUseCaseTest {

    private val emailParser = StubbedEmailParser()
    private val emailRepository = MockEmailRepository()
    private val emailUniquenessValidator = emailRepository
    private val eventPublisher = MockEventPublisher()
    private val useCase = RegistreerEmailUseCase(emailParser, emailUniquenessValidator, emailRepository, eventPublisher)

    @Nested
    internal inner class `when a new email is registered`: FailingRegistreerEmailPresenter() {

        private var emailRegisteredCalled = false
        private lateinit var presentedEmailId: EmailId
        private lateinit var rawEmail: RawEmail

        @BeforeEach
        fun setUp() {
            emailParser.setEmailToReturn(aParsedEmail)

            rawEmail = RawEmailTestBuilder().build()
            useCase.execute(
                command = RegistreerEmailCommand(rawEmail),
                presenter = this
            )
        }

        @Test
        fun `then the parsed email is stored`() {
            assertThat(emailRepository.storedItems.values).anySatisfy { email ->
                assertThat(email.fromEmail).isEqualTo(aParsedEmail.from)
                assertThat(email.replyToEmail).isEqualTo(aParsedEmail.replyTo)
                assertThat(email.emailMessageId).isEqualTo(aParsedEmail.messageId)
                assertThat(email.status).isInstanceOf(EmailStatus.Geregistreerd::class.java)
            }
            assertThat(emailParser.usedEmail).isEqualTo(rawEmail.content)
        }

        @Test
        fun `then an EmailGeregistreerdEvent is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(
                EmailGeregistreerd(
                    emailId = presentedEmailId,
                    bestandsnaam = rawEmail.bestandsnaam,
                    aard = rawEmail.emailAard,
                )
            )
        }

        @Test
        fun `then emailRegisteredCalled is presented`() {
            assertThat(emailRegisteredCalled).isTrue()
            assertThat(presentedEmailId).isNotNull()
        }

        override fun emailRegistered(emailId: EmailId) {
            this.emailRegisteredCalled = true
            this.presentedEmailId = emailId
        }
    }

    @Nested
    internal inner class `when an aready registered email is registered again`: FailingRegistreerEmailPresenter() {

        private var emailAlreadyRegisteredCalled = false
        private lateinit var presentedEmailId: EmailId
        private lateinit var rawEmail: RawEmail

        @BeforeEach
        fun setUp() {
            emailRepository.save(
                Email.fromSnapshot(
                    EmailSnapshot(
                        emailId = EmailId.createNew(),
                        fromEmail = aParsedEmail.from,
                        replyToEmail = aParsedEmail.replyTo,
                        returnPathEmail = aParsedEmail.returnPath,
                        emailMessageId = aParsedEmail.messageId
                    )
                )
            )
            emailParser.setEmailToReturn(aParsedEmail)
            assertThat(emailRepository.storedItems).hasSize(1)

            rawEmail = RawEmailTestBuilder().build()
            useCase.execute(
                command = RegistreerEmailCommand(rawEmail),
                presenter = this
            )
        }

        @Test
        fun `then the email is not stored again`() {
            assertThat(emailRepository.storedItems).hasSize(1)
            assertThat(emailParser.usedEmail).isEqualTo(rawEmail.content)
        }

        @Test
        fun `then no event is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then emailAlreadyRegistered is presented`() {
            assertThat(emailAlreadyRegisteredCalled).isTrue()
            assertThat(presentedEmailId).isNotNull()
        }

        override fun emailAlreadyRegistered(emailId: EmailId) {
            this.emailAlreadyRegisteredCalled = true
            this.presentedEmailId = emailId
        }

    }

    @Nested
    internal inner class `when an email is registered but parsing fails`: FailingRegistreerEmailPresenter() {

        private var emailRegistrationFailedCalled = false

        private lateinit var rawEmail: RawEmail

        @BeforeEach
        fun setUp() {
            emailParser.failOnNextCall()

            rawEmail = RawEmailTestBuilder().build()
            useCase.execute(
                command = RegistreerEmailCommand(rawEmail),
                presenter = this
            )
        }

        @Test
        fun `then no email is stored`() {
            assertThat(emailRepository.storedItems).isEmpty()
            assertThat(emailParser.usedEmail).isEqualTo(rawEmail.content)
        }

        @Test
        fun `then no event is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then emailRegistrationFailed is presented`() {
            assertThat(emailRegistrationFailedCalled).isTrue()
        }

        override fun emailRegistrationFailed() {
            this.emailRegistrationFailedCalled = true
        }

    }

    private val aParsedEmail = ParsedEmail(
        replyTo = EmailAdres.noOps,
        from = EmailAdres.noOps,
        returnPath = EmailAdres.noOps,
        messageId = EmailMessageId.random()
    )

    open class FailingRegistreerEmailPresenter: RegistreerEmailPresenter {
        override fun emailRegistered(emailId: EmailId) = TestUtils.fail()
        override fun emailAlreadyRegistered(emailId: EmailId) = TestUtils.fail()
        override fun emailRegistrationFailed() = TestUtils.fail()
    }
}
