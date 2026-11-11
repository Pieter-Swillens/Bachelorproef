package io.pieterswillens.poc.use.cases

import io.pieterswillens.poc.domain.*
import io.pieterswillens.poc.testdoubles.MockEmailRepository
import io.pieterswillens.poc.testdoubles.RawEmailTestBuilder
import io.pieterswillens.poc.testdoubles.TestUtils
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
    private val useCase = RegistreerEmailUseCase(emailParser, emailUniquenessValidator, emailRepository)

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
                registreerEmailCommand = RegistreerEmailCommand(rawEmail),
                presenter = this
            )
        }

        @Test
        fun `then the parsed email is stored`() {
            assertThat(emailRepository.storedItems).anySatisfy { email ->
                assertThat(email.fromEmail).isEqualTo(aParsedEmail.from)
                assertThat(email.replyToEmail).isEqualTo(aParsedEmail.replyTo)
                assertThat(email.emailMessageId).isEqualTo(aParsedEmail.messageId)
            }
            assertThat(emailParser.usedEmail).isEqualTo(rawEmail.content)
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
                        emailMessageId = aParsedEmail.messageId
                    )
                )
            )
            emailParser.setEmailToReturn(aParsedEmail)
            assertThat(emailRepository.storedItems).hasSize(1)

            rawEmail = RawEmailTestBuilder().build()
            useCase.execute(
                registreerEmailCommand = RegistreerEmailCommand(rawEmail),
                presenter = this
            )
        }

        @Test
        fun `then the email is not stored again`() {
            assertThat(emailRepository.storedItems).hasSize(1)
            assertThat(emailParser.usedEmail).isEqualTo(rawEmail.content)
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
                registreerEmailCommand = RegistreerEmailCommand(rawEmail),
                presenter = this
            )
        }

        @Test
        fun `then no email is stored`() {
            assertThat(emailRepository.storedItems).isEmpty()
            assertThat(emailParser.usedEmail).isEqualTo(rawEmail.content)
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
        messageId = EmailMessageId.random()
    )

    open class FailingRegistreerEmailPresenter: RegistreerEmailPresenter {
        override fun emailRegistered(emailId: EmailId) = TestUtils.fail()
        override fun emailAlreadyRegistered(emailId: EmailId) = TestUtils.fail()
        override fun emailRegistrationFailed() = TestUtils.fail()
    }
}

class StubbedEmailParser : EmailParser {
    private var failOnNext = false
    
    var usedEmail: ByteArray? = null
        private set
    private var toReturn = ParsedEmail(
        replyTo = EmailAdres.noOps,
        from = EmailAdres.noOps,
        messageId = EmailMessageId.random()
    )

    override fun parse(email: ByteArray): EmailParsingResult {
        this.usedEmail = email
        return if (failOnNext) {
            EmailParsingResult.Mislukt
        } else {
            EmailParsingResult.Ok(toReturn)
        }
    }

    fun setEmailToReturn(parsedEmail: ParsedEmail) {
        toReturn = parsedEmail
    }

    fun failOnNextCall() {
        this.failOnNext = true
    }

}
