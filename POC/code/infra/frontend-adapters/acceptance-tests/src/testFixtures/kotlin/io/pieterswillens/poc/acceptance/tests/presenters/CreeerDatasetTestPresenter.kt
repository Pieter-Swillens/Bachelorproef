package io.pieterswillens.poc.acceptance.tests.presenters

import io.pieterswillens.poc.api.CreeerDatasetPresenter
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed

class CreeerDatasetTestPresenter: CreeerDatasetPresenter {
    lateinit var presentedDatasetId: DatasetId

    override fun datasetGecreeerd(datasetId: DatasetId, seed: Seed) {
        this.presentedDatasetId = datasetId
    }

    override fun datasetCreatieMisluktDoorTeKleineSampleset() = TestUtils.fail()
    override fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard() = TestUtils.fail()
}
