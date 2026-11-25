package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.domain.dataset.presenter.CreeerDatasetDomainPresenter
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed
import kotlin.math.roundToInt
import kotlin.random.Random

class Dataset private constructor(
    val datasetId: DatasetId,
    val seed: Seed,
    val trainingSet: Set<Sample>,
    val evaluatieSet: Set<Sample>,
    status: DatasetStatus
) {

    var status: DatasetStatus = status
        private set

    init {
        require(trainingSet.isNotEmpty() && evaluatieSet.isNotEmpty()) { "TrainingSet and EvaluatieSet may not be empty" }

        val sampleset = trainingSet + evaluatieSet
        val samplesByAard = sampleset.groupBy { it.aard }
        val samplesetContainsEnoughEmailAardExamples = EmailAard.entries.map {
            samplesByAard[it]?.let { values -> values.size >= MINIMAL_EXAMPLES_FOR_EMAILAARD } ?: false
        }.all { it }

        require(sampleset.size >= MINIMAL_SAMPLESET_SIZE_FOR_DATASET_CREATION) { "Dataset needs to contain at least $MINIMAL_SAMPLESET_SIZE_FOR_DATASET_CREATION entries" }
        require(samplesetContainsEnoughEmailAardExamples) { "Dataset needs at least $MINIMAL_EXAMPLES_FOR_EMAILAARD examples of each EmailAard" }
    }

    companion object {
        fun creeer(
            seed: Seed,
            geefSamplesService: GeefSamplesService,
            presenter: CreeerDatasetDomainPresenter
        ): Dataset? {
            val samples = geefSamplesService.geefSamples()
            if (samples.size < MINIMAL_SAMPLESET_SIZE_FOR_DATASET_CREATION) {
                presenter.datasetCreatieMisluktDoorTeKleineSampleset()
                return null
            }

            val samplesPerAard = samples.groupBy { it.aard }
            val heeftTeWeinigVoorbeelden = EmailAard.entries.any { aard -> samplesPerAard[aard].orEmpty().size < MINIMAL_EXAMPLES_FOR_EMAILAARD }
            if (heeftTeWeinigVoorbeelden) {
                presenter.datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard()
                return null
            }

            val trainingSet = mutableSetOf<Sample>()
            val evaluatieSet = mutableSetOf<Sample>()

            val random = Random(seed.value)
            for (emailAard in EmailAard.entries) {
                val geseedeSamples = samplesPerAard[emailAard].orEmpty()
                    .sortedBy { it.bestandsnaam }
                    .shuffled(random)

                val treshhold = (geseedeSamples.count() * TRAINING_SET_RATIO).roundToInt()
                trainingSet += geseedeSamples.take(treshhold)
                evaluatieSet += geseedeSamples.drop(treshhold)
            }

            return Dataset(
                datasetId = DatasetId.createNew(),
                seed = seed,
                trainingSet = trainingSet,
                evaluatieSet = evaluatieSet,
                status = DatasetStatus.Gecreeerd
            )
        }

        fun fromSnapshot(snapshot: DatasetSnapshot) = Dataset(
            datasetId = snapshot.datasetId,
            seed = snapshot.seed,
            trainingSet = snapshot.trainingSet,
            evaluatieSet = snapshot.evaluatieSet,
            status = snapshot.status
        )

        private const val TRAINING_SET_RATIO = 0.7
        private const val MINIMAL_SAMPLESET_SIZE_FOR_DATASET_CREATION = 10
        private const val MINIMAL_EXAMPLES_FOR_EMAILAARD = 2
    }
}