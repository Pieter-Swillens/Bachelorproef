package io.pieterswillens.poc.api

import io.pieterswillens.poc.vocabulary.Presenter
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed

fun interface CreeerDataset {
    fun creeerDataset(seed: Seed, presenter: CreeerDatasetPresenter)
}

interface CreeerDatasetPresenter: Presenter {
    fun datasetGecreeerd(datasetId: DatasetId, seed: Seed)
    fun datasetCreatieMisluktDoorTeKleineSampleset()
    fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard()
}