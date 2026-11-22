package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.domain.dataset.presenter.CreeerDatasetDomainPresenter
import io.pieterswillens.poc.domain.email.EmailStatus
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

        private const val TRAINING_SET_RATIO = 0.7
        private const val MINIMAL_SAMPLESET_SIZE_FOR_DATASET_CREATION = 10
        private const val MINIMAL_EXAMPLES_FOR_EMAILAARD = 2
    }
}