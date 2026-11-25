package io.pieterswillens.poc.testdoubles.dataset.testbuilder

import io.pieterswillens.poc.domain.dataset.Dataset
import io.pieterswillens.poc.domain.dataset.DatasetSnapshot
import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.testdoubles.dataset.TestSampleSetGenerator
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed

class DatasetTestBuilder {

    private var datasetId = DatasetId.createNew()
    private var seed: Seed = Seed.DEFAULT
    private var trainingsSet: Set<Sample> = TestSampleSetGenerator.generateSamples(70)
    private var evaluatieSet: Set<Sample> = TestSampleSetGenerator.generateSamples(30)

    fun with(
        datasetId: DatasetId? = null,
        seed: Seed? = null,
        trainingsSet: Set<Sample>? = null,
        evaluatieSet: Set<Sample>? = null,
    ): DatasetTestBuilder {
        datasetId?.let { this.datasetId = datasetId }
        seed?.let { this.seed = seed }
        trainingsSet?.let { this.trainingsSet = trainingsSet }
        evaluatieSet?.let { this.evaluatieSet = evaluatieSet }

        return this
    }

    fun build(): Dataset {
        return Dataset.fromSnapshot(
            DatasetSnapshot(
                datasetId = datasetId,
                seed = seed,
                trainingSet = trainingsSet,
                evaluatieSet = evaluatieSet
            )
        )
    }
}
