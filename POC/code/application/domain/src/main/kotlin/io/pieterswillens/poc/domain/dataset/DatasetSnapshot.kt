package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed

data class DatasetSnapshot(
    val datasetId: DatasetId,
    val seed: Seed,
    val trainingSet: Set<Sample>,
    val evaluatieSet: Set<Sample>,
    val status: DatasetStatus = DatasetStatus.Gecreeerd
)