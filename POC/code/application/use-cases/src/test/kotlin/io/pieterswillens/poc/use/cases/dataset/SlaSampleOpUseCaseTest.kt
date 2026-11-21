package io.pieterswillens.poc.use.cases.dataset

import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.domain.dataset.events.SampleOpgeslagen
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.testdoubles.dataset.MockSampleRepository
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a use case to SlaSampleOp")
class SlaSampleOpUseCaseTest {

    private val eventPublisher = MockEventPublisher()
    private val sampleRepository = MockSampleRepository()
    private val useCase = SlaSampleOpUseCase(sampleRepository, eventPublisher)

    @Nested
    internal inner class `when succesfully saving a sample`: FailingSlaSampleOpPresenter(){

        private lateinit var presentedEmailId: EmailId
        private lateinit var presentedBestandsnaam: String
        private lateinit var presentedAard: EmailAard
        private var sampleOpgeslagenCalled = false

        private val emailId = EmailId.createNew()
        private val bestandsnaam = "email.eml"
        private val aard = EmailAard.LEGITIEM

        @BeforeEach
        fun setUp() {
            useCase.execute(SlaSampleOpCommand(emailId, bestandsnaam, aard), this)
        }

        @Test
        fun `then the sample is persisted`() {
            assertThat(sampleRepository.storedItems).anySatisfy { id, sample ->
                assertThat(id).isEqualTo(emailId)
                assertThat(sample.bestandsnaam).isEqualTo(bestandsnaam)
                assertThat(sample.aard).isEqualTo(aard)
            }
        }

        @Test
        fun `then a SampleOpgeslagen event is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(SampleOpgeslagen(emailId))
        }

        @Test
        fun `then sampleOpgeslagen is presented`() {
            assertThat(sampleOpgeslagenCalled).isTrue()
            assertThat(presentedEmailId).isEqualTo(emailId)
            assertThat(presentedBestandsnaam).isEqualTo(bestandsnaam)
            assertThat(presentedAard).isEqualTo(aard)
        }

        override fun sampleOpgeslagen(emailId: EmailId, bestandsnaam: String, aard: EmailAard) {
            this.sampleOpgeslagenCalled = true
            this.presentedEmailId = emailId
            this.presentedBestandsnaam = bestandsnaam
            this.presentedAard = aard
        }
    }

    @Nested
    internal inner class `when sample already exists`: FailingSlaSampleOpPresenter(){

        private lateinit var presentedEmailId: EmailId
        private var sampleAlreadyExistsCalled = false

        private val alreadyExistingSample = Sample(
            emailId = EmailId.createNew(),
            bestandsnaam = "email.eml",
            aard = EmailAard.LEGITIEM
        )

        @BeforeEach
        fun setUp() {
            sampleRepository.save(alreadyExistingSample)

            useCase.execute(SlaSampleOpCommand(alreadyExistingSample.emailId, "email.eml", EmailAard.PHISHING), this)
        }

        @Test
        fun `then the sample is not persisted`() {
            assertThat(sampleRepository.storedItems[alreadyExistingSample.emailId]).isEqualTo(alreadyExistingSample)
        }

        @Test
        fun `then no SampleOpgeslagen event is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then sampleAlreadyExists is presented`() {
            assertThat(sampleAlreadyExistsCalled).isTrue()
            assertThat(presentedEmailId).isNotNull()
        }

        override fun sampleAlreadyExists(emailId: EmailId) {
            this.sampleAlreadyExistsCalled = true
            this.presentedEmailId = emailId
        }
    }

    open class FailingSlaSampleOpPresenter: SlaSampleOpPresenter {
        override fun sampleOpgeslagen(emailId: EmailId, bestandsnaam: String, aard: EmailAard) = TestUtils.fail()
        override fun sampleAlreadyExists(emailId: EmailId) = TestUtils.fail()
    }
}