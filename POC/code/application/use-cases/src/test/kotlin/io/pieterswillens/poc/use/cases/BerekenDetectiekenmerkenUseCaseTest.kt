package io.pieterswillens.poc.use.cases

import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkId
import io.pieterswillens.poc.domain.email.detectiekenmerken.ExtractieResult
import io.pieterswillens.poc.domain.email.events.DetectiekenmerkenBerekend
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.testdoubles.email.MockDetectiekenmerken
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.testbuilders.GeregistreerdeEmailTestBuilder
import io.pieterswillens.poc.vocabulary.EmailId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a use case to to bereken the detectiekenmerken of an email")
class BerekenDetectiekenmerkenUseCaseTest {

    private val detectiekenmerken = MockDetectiekenmerken()
    private val emailRepository = MockEmailRepository()
    private val eventPublisher = MockEventPublisher()
    private val useCase = BerekenDetectiekenmerkenUseCase(detectiekenmerken, emailRepository, eventPublisher,)

    @Nested
    internal inner class `when berekening the detectiekenmerken for an email`: FailingBerekenDetectiekenmerkenPresenter() {

        private lateinit var usedEmailId: EmailId
        private var detectieKenmerkenSuccesvolBerekendCalled = false

        private val emailId = EmailId.createNew()
        private val expectedKenmerkenprofiel = mapOf(DetectiekenmerkId.A1_FROM_WIJKT_AF_VAN_REPLY_TO to ExtractieResult.NietAfwijkend)

        @BeforeEach
        fun setUp() {
            val email = GeregistreerdeEmailTestBuilder()
                .with(emailId = emailId)
                .build()
            emailRepository.save(email)
            detectiekenmerken.kenmerkenProfiel = expectedKenmerkenprofiel

            assertThat(email.status).isInstanceOf(EmailStatus.Geregistreerd::class.java)
            useCase.execute(BerekenDetectiekenmerkenCommand(emailId = emailId), this)
        }

        @Test
        fun `then the kenmerkenprofiel is successfully saved for the email`() {
            assertThat(emailRepository.storedItems.values).anySatisfy { email ->
                assertThat(email.emailId).isEqualTo(emailId)
                assertThat(email.status).isInstanceOfSatisfying(EmailStatus.DetectiekenmerkenBerekend::class.java) {
                    assertThat(it.kenmerkenprofiel).isEqualTo(expectedKenmerkenprofiel)
                }
            }
        }

        @Test
        fun `then an DetectiekenmerkenBerekend event is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(DetectiekenmerkenBerekend(emailId))
        }

        @Test
        fun `then success is presented`() {
            assertThat(detectieKenmerkenSuccesvolBerekendCalled).isTrue()
            assertThat(usedEmailId).isEqualTo(emailId)
        }

        override fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId) {
            this.detectieKenmerkenSuccesvolBerekendCalled = true
            this.usedEmailId = emailId
        }
    }

    @Nested
    internal inner class `when berekening the detectiekenmerken for a non existing email`: FailingBerekenDetectiekenmerkenPresenter() {

        private var emailNotFoundCalled = false

        @BeforeEach
        fun setUp() {
            useCase.execute(BerekenDetectiekenmerkenCommand(EmailId.createNew()), this)
        }

        @Test
        fun `then no detectiekenmerken are berekend and no new email is stored`() {
            assertThat(emailRepository.storedItems.values).isEmpty()
        }

        @Test
        fun `then no DetectiekenmerkenBerekend event is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then emailNotFound is presented`() {
            assertThat(emailNotFoundCalled).isTrue()
        }

        override fun emailNotFound(emailId: EmailId) {
            this.emailNotFoundCalled = true
        }
    }

    @Nested
    internal inner class `when the detectiekenmerken for an email are already berekend`: FailingBerekenDetectiekenmerkenPresenter() {

        private lateinit var usedEmailId: EmailId
        private var detectieKenmerkenSuccesvolBerekendCalled = false

        private val emailId = EmailId.createNew()
        private val initialKenmerkenprofiel = mapOf(DetectiekenmerkId.A1_FROM_WIJKT_AF_VAN_REPLY_TO to ExtractieResult.NietBepaalbaar)

        @BeforeEach
        fun setUp() {
            val email = GeregistreerdeEmailTestBuilder()
                .with(emailId)
                .detectiekenmerkenBerekend()
                .with(initialKenmerkenprofiel)
                .build()
            emailRepository.save(email)
            assertThat(email.status).isInstanceOfSatisfying(EmailStatus.DetectiekenmerkenBerekend::class.java) {
                assertThat(it.kenmerkenprofiel).isEqualTo(initialKenmerkenprofiel)
            }

            useCase.execute(BerekenDetectiekenmerkenCommand(emailId), this)
        }

        @Test
        fun `then detectiekenmerken are herberekend`() {
            assertThat(emailRepository.storedItems.values).anySatisfy { email ->
                assertThat(email.emailId).isEqualTo(emailId)
                assertThat(email.status).isInstanceOfSatisfying(EmailStatus.DetectiekenmerkenBerekend::class.java) {
                    assertThat(it.kenmerkenprofiel).isNotEqualTo(initialKenmerkenprofiel)
                }
            }
        }

        @Test
        fun `then a DetectiekenmerkenBerekend event is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(DetectiekenmerkenBerekend(emailId))
        }

        @Test
        fun `then success is presented`() {
            assertThat(detectieKenmerkenSuccesvolBerekendCalled).isTrue()
            assertThat(usedEmailId).isEqualTo(emailId)
        }

        override fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId) {
            this.detectieKenmerkenSuccesvolBerekendCalled = true
            this.usedEmailId = emailId
        }
    }

    open class FailingBerekenDetectiekenmerkenPresenter: BerekenDetectiekenmerkenPresenter {
        override fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId) = TestUtils.fail()
        override fun emailNotFound(emailId: EmailId) = TestUtils.fail()
    }
}
