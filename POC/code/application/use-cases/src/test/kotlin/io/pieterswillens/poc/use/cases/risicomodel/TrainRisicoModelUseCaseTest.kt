package io.pieterswillens.poc.use.cases.risicomodel

import io.pieterswillens.poc.domain.risicomodel.GeefTrainingsDatasetResponse
import io.pieterswillens.poc.domain.risicomodel.events.RisicoModelGetraind
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.testdoubles.dataset.MockDatasetRepository
import io.pieterswillens.poc.testdoubles.dataset.MockGeefTrainingsDatasetService
import io.pieterswillens.poc.testdoubles.dataset.testbuilder.DatasetTestBuilder
import io.pieterswillens.poc.testdoubles.risicomodel.MockRisicoModelRepository
import io.pieterswillens.poc.testdoubles.risicomodel.MockRisicoModelTrainer
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a use case to train the detection model model")
class TrainRisicoModelUseCaseTest {

    private val eventPublisher = MockEventPublisher()
    private val datasetRepository = MockDatasetRepository()
    private val geefTrainingsDatasetService = MockGeefTrainingsDatasetService(datasetRepository)
    private val risicoModelRepository = MockRisicoModelRepository()
    private val risicoModelTrainer = MockRisicoModelTrainer()
    private val useCase = TrainRisicoModelUseCase(
        risicoModelTrainer = risicoModelTrainer,
        risicoModelRepository = risicoModelRepository,
        geefTrainingsDatasetService = geefTrainingsDatasetService,
        eventPublisher = eventPublisher
    )

    private val datasetId = DatasetId.createNew()

    @BeforeEach
    fun setUp() {
        datasetRepository.save(
            DatasetTestBuilder().with(datasetId = datasetId).build()
        )
    }

    @Nested
    internal inner class `when training the model`: FailingTrainRisicoModelPresenter() {

        private var risicoModelGetraindCalled = false
        private lateinit var risicoModelId: RisicoModelId

        @BeforeEach
        fun setUp() {
            useCase.execute(TrainRisicoModelCommand(datasetId), this)
        }

        @Test
        fun `then the model is saved`() {
            assertThat(risicoModelRepository.storedItems.values).anySatisfy { risicoModel ->
                assertThat(risicoModel.risicoModelId).isEqualTo(risicoModelId)
            }
        }

        @Test
        fun `then the risicoModelTrainer is called`() {
            val expectedTrainingsData = geefTrainingsDatasetService.geefTrainingsSet(datasetId) as GeefTrainingsDatasetResponse.Ok

            assertThat(risicoModelTrainer.trainingsData).isEqualTo(expectedTrainingsData.trainingsData)
        }

        @Test
        fun `then a RisicoModelGetraind is published`() {
            assertThat(eventPublisher.lastStoredEvent).isEqualTo(RisicoModelGetraind(risicoModelId = risicoModelId))
        }

        @Test
        fun `then risicoModelGetraind is presented`() {
            assertThat(risicoModelGetraindCalled).isTrue()
        }

        override fun risicoModelGetraind(risicoModelId: RisicoModelId) {
            this.risicoModelGetraindCalled = true
            this.risicoModelId = risicoModelId
        }
    }

    @Nested
    internal inner class `when the dataset does not exist`: FailingTrainRisicoModelPresenter() {

        private var datasetNotFoundCalled = false

        @BeforeEach
        fun setUp() {
            val randomDatasetId = DatasetId.createNew()
            useCase.execute(TrainRisicoModelCommand(randomDatasetId), this)
        }

        @Test
        fun `then no model is saved`() {
            assertThat(risicoModelRepository.storedItems).isEmpty()
        }

        @Test
        fun `then the risicoModelTrainer is not called`() {
            assertThat(risicoModelTrainer.trainingsData).isNull()
        }

        @Test
        fun `then a no RisicoModelGetraind is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then datasetNotFound is presented`() {
            assertThat(datasetNotFoundCalled).isTrue()
        }

        override fun datasetNotFound(datasetId: DatasetId) {
            this.datasetNotFoundCalled = true
        }
    }

    @Nested
    internal inner class `when the dataset contains emails for which the detectiekenmerken are not yet berekend`: FailingTrainRisicoModelPresenter() {

        private var datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijnCalled = false

        @BeforeEach
        fun setUp() {
            geefTrainingsDatasetService.detectiekenmerkenNogNietBerekend = true
            useCase.execute(TrainRisicoModelCommand(datasetId), this)
        }

        @Test
        fun `then no model is saved`() {
            assertThat(risicoModelRepository.storedItems).isEmpty()
        }

        @Test
        fun `then the risicoModelTrainer is not called`() {
            assertThat(risicoModelTrainer.trainingsData).isNull()
        }

        @Test
        fun `then a no RisicoModelGetraind is published`() {
            assertThat(eventPublisher.hasNoEventPublished()).isTrue()
        }

        @Test
        fun `then datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijn is presented`() {
            assertThat(datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijnCalled).isTrue()
        }

        override fun datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijn(datasetId: DatasetId) {
            this.datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijnCalled = true
        }
    }

    open class FailingTrainRisicoModelPresenter: TrainRisicoModelPresenter {
        override fun risicoModelGetraind(risicoModelId: RisicoModelId) = TestUtils.fail()
        override fun datasetNotFound(datasetId: DatasetId) = TestUtils.fail()
        override fun datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijn(datasetId: DatasetId) = TestUtils.fail()
    }
}
