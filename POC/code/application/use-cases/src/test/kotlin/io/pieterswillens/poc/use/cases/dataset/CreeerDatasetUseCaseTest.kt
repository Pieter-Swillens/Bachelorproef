package io.pieterswillens.poc.use.cases.dataset

import io.pieterswillens.poc.api.CreeerDatasetPresenter
import io.pieterswillens.poc.domain.dataset.Dataset
import io.pieterswillens.poc.domain.dataset.DatasetStatus
import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.domain.dataset.events.DatasetGecreeerd
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.testdoubles.dataset.MockDatasetRepository
import io.pieterswillens.poc.testdoubles.dataset.MockSampleRepository
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedClass
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream
import kotlin.math.roundToInt

@DisplayName("Given a use case to creëer a dataset")
class CreeerDatasetUseCaseTest {

    private val trainingSizeRatio = 0.7
    private val evaluationSizeRatio = 1 - trainingSizeRatio

    private val eventPublisher = MockEventPublisher()
    private val geefSamplesService = MockSampleRepository()
    private val datasetRepository = MockDatasetRepository()
    private val useCase = CreeerDatasetUseCase(
        geefSamplesService,
        datasetRepository,
        eventPublisher
    )

    @Nested
    @ParameterizedClass
    @ArgumentsSource(ValidSampleSetsTestParams::class)
    internal inner class `when enough sample are available to creeer a dataset`(
        private val scenario: TestScenario
    ): FailingCreeerDatasetUseCasePresenter() {

        private val sampleSize = scenario.sampleSize
        private val scenarioAmountOfProvidedSamples = scenario.samples.size

        private lateinit var presentedDatasetId: DatasetId
        private lateinit var presentedSeed: Seed

        private var datasetGecreeerdCalled = false

        @BeforeEach
        fun setUp() {
            scenario.samples.forEach { geefSamplesService.save(it) }
            geefSamplesService.fill(size = sampleSize - scenarioAmountOfProvidedSamples, emailAard = scenario.emailAardToFillSampleSetWith)
            useCase.execute(CreeerDatasetCommand(Seed.DEFAULT), this)
        }

        @Test
        fun `then the dataset is created and stored`() {
            assertThat(datasetRepository.get(presentedDatasetId))
                .isNotNull()
                .satisfies({ dataset ->
                    assertThat(dataset!!.datasetId).isEqualTo(presentedDatasetId)
                    assertThat(dataset.seed).isEqualTo(Seed.DEFAULT)
                    assertThat(dataset.status).isEqualTo(DatasetStatus.Gecreeerd)
                })
        }

        @Test
        fun `Then the samples are partitioned into training and evaluation subsets via a 70 30 stratified split`() {
            assertThat(datasetRepository.get(presentedDatasetId))
                .isNotNull()
                .satisfies({ dataset ->
                    assertThat(dataset!!.trainingSet.size).isCloseTo((sampleSize * trainingSizeRatio).roundToInt(), within(1))
                    assertThat(dataset.evaluatieSet.size).isCloseTo((sampleSize * evaluationSizeRatio).roundToInt(), within(1))
                    assertThat(dataset.trainingSet).doesNotContainAnyElementsOf(dataset.evaluatieSet)
                    assertStratification(dataset, sampleSize)
                })
        }

        @Test
        fun `then a DatasetGecreeerd event is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(
                DatasetGecreeerd(datasetId = presentedDatasetId)
            )
        }

        @Test
        fun `then datasetGecreeerd is presented`() {
            assertThat(datasetGecreeerdCalled).isTrue()
            assertThat(presentedDatasetId).isNotNull()
            assertThat(presentedSeed).isEqualTo(Seed.DEFAULT)
        }

        override fun datasetGecreeerd(datasetId: DatasetId, seed: Seed) {
            datasetGecreeerdCalled = true
            this.presentedDatasetId = datasetId
            this.presentedSeed = seed
        }

    }

    @Nested
    internal inner class `when not enough samples are available to creeer a dataset`: FailingCreeerDatasetUseCasePresenter(){

        private val notEnoughSamples = 9

        private var datasetCreatieMisluktDoorTeKleineSamplesetCalled = false

        @BeforeEach
        fun setUp() {
            geefSamplesService.fill(size = notEnoughSamples)
            useCase.execute(CreeerDatasetCommand(Seed.DEFAULT), this)
        }

        @Test
        fun `then no dataset is created or stored`() {
            assertThat(datasetRepository.storedItems).isEmpty()
        }

        @Test
        fun `then no DatasetGecreeerd event is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then datasetCreatieMisluktDoorTeKleineSampleset is presented`() {
            assertThat(datasetCreatieMisluktDoorTeKleineSamplesetCalled).isTrue()
        }

        override fun datasetCreatieMisluktDoorTeKleineSampleset() {
            datasetCreatieMisluktDoorTeKleineSamplesetCalled = true
        }

    }

    @Nested
    @ParameterizedClass
    @ArgumentsSource(TooLittleExamplesForEmailAardTestParams::class)
    internal inner class `when not enough samples of a certain emailAard are available to creeer a dataset`(
        val testScenario: TestScenario
    ): FailingCreeerDatasetUseCasePresenter(){

        private val sampleSize = testScenario.sampleSize

        private var datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAardCalled = false

        @BeforeEach
        fun setUp() {
            testScenario.samples.forEach { geefSamplesService.save(it) }
            geefSamplesService.fill(size = sampleSize, emailAard = testScenario.emailAardToFillSampleSetWith)

            useCase.execute(CreeerDatasetCommand(Seed.DEFAULT), this)
        }

        @Test
        fun `then no dataset is created or stored`() {
            assertThat(datasetRepository.storedItems).isEmpty()
        }

        @Test
        fun `then no DatasetGecreeerd event is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard is presented`() {
            assertThat(datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAardCalled).isTrue()
        }

        override fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard() {
            datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAardCalled = true
        }

    }

    private fun assertStratification(dataset: Dataset, sampleSize: Int) {
        val samples = geefSamplesService.storedItems.values // TODO: Use some sort of list all
        assertThat(samples).hasSize(sampleSize)
        val amountOfLegitimateMails = samples.count { it.aard == EmailAard.LEGITIEM }

        val legitRatio = amountOfLegitimateMails.toDouble() / sampleSize

        val mailsInTraining = (sampleSize * trainingSizeRatio).toInt()
        val mailsInEvaluation = (sampleSize * evaluationSizeRatio).toInt()

        val actualTrainingLegit = dataset.trainingSet.count { it.aard == EmailAard.LEGITIEM }
        val actualEvaluationLegit = dataset.evaluatieSet.count { it.aard == EmailAard.LEGITIEM }
        val actualEvaluationPhishing = dataset.evaluatieSet.count { it.aard == EmailAard.PHISHING }
        val actualTrainingPhishing = dataset.trainingSet.count { it.aard == EmailAard.PHISHING }

        val expectedTrainingLegit = (mailsInTraining * legitRatio).roundToInt()
        val expectedEvaluationLegit = (mailsInEvaluation * legitRatio).roundToInt()
        val expectedTrainingPhishing = mailsInTraining - expectedTrainingLegit
        val expectedEvaluationPhishing = mailsInEvaluation - expectedEvaluationLegit

        assertThat(actualTrainingLegit).isCloseTo(expectedTrainingLegit, within(1))
        assertThat(actualEvaluationLegit).isCloseTo(expectedEvaluationLegit, within(1))
        assertThat(actualTrainingPhishing).isCloseTo(expectedTrainingPhishing, within(1))
        assertThat(actualEvaluationPhishing).isCloseTo(expectedEvaluationPhishing, within(1))
    }

    open class FailingCreeerDatasetUseCasePresenter: CreeerDatasetPresenter {
        override fun datasetGecreeerd(datasetId: DatasetId, seed: Seed) = TestUtils.fail()
        override fun datasetCreatieMisluktDoorTeKleineSampleset() = TestUtils.fail()
        override fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard() = TestUtils.fail()
    }

    class ValidSampleSetsTestParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    Named.of("for a normal sampleset", TestScenario(sampleSize = 1000))
                ),
                Arguments.of(
                    Named.of("for a minimal sampleset", TestScenario(sampleSize = 10))
                ),
                Arguments.of(
                    Named.of(
                        "for a sampleset containing only 2 phishingmails",
                        TestScenario(
                            sampleSize = 10,
                            samples = listOf(
                                Sample(emailId = EmailId.createNew(), bestandsnaam = "phishing1.eml", aard = EmailAard.PHISHING),
                                Sample(emailId = EmailId.createNew(), bestandsnaam = "phishing2.eml", aard = EmailAard.PHISHING)
                            ),
                            emailAardToFillSampleSetWith = EmailAard.LEGITIEM
                        )
                    )
                ),
                Arguments.of(
                    Named.of(
                        "for a sampleset containing only 2 legit mails",
                        TestScenario(
                            sampleSize = 10,
                            samples = listOf(
                                Sample(emailId = EmailId.createNew(), bestandsnaam = "legit1.eml", aard = EmailAard.LEGITIEM),
                                Sample(emailId = EmailId.createNew(), bestandsnaam = "legit2.eml", aard = EmailAard.LEGITIEM)
                            ),
                            emailAardToFillSampleSetWith = EmailAard.PHISHING
                        )
                    )
                ),
            )
        }
    }

    class TooLittleExamplesForEmailAardTestParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(Named.of("for a sampleset without any phishingmail", TestScenario(emailAardToFillSampleSetWith = EmailAard.LEGITIEM))),
                Arguments.of(Named.of("for a sampleset without any legit mail", TestScenario(emailAardToFillSampleSetWith = EmailAard.PHISHING))),
                Arguments.of(Named.of("for a sampleset with only one phishingmail", TestScenario(samples = listOf(Sample(EmailId.createNew(), "phishingMail.eml", EmailAard.PHISHING)), emailAardToFillSampleSetWith = EmailAard.LEGITIEM))),
                Arguments.of(Named.of("for a sampleset with only one legit mail", TestScenario(samples = listOf(Sample(EmailId.createNew(), "legitMail.eml", EmailAard.LEGITIEM)), emailAardToFillSampleSetWith = EmailAard.PHISHING)))
            )
        }
    }

    internal data class TestScenario(
        val sampleSize: Int = 100,
        val samples: List<Sample> = emptyList(),
        val emailAardToFillSampleSetWith: EmailAard? = null,
    )

}
