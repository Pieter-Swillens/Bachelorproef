package io.pieterswillens.poc.cli.adapter.mocks

import io.pieterswillens.poc.api.CreeerDataset
import io.pieterswillens.poc.api.CreeerDatasetPresenter
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed

class MockCreeerDataset: CreeerDataset, MockApplicationApi<MockCreeerDataset.TestAction> {
    private var nextAction: TestAction? = null

    var usedSeed: Seed? = null
        private set

    override fun creeerDataset(seed: Seed, presenter: CreeerDatasetPresenter) {
        this.usedSeed = seed
        when (nextAction) {
            TestAction.SUCCESSFULLY_CREATED -> presenter.datasetGecreeerd(DatasetId.createNew(), seed)
            TestAction.FAILED_SAMPLESET_TOO_SMALL -> presenter.datasetCreatieMisluktDoorTeKleineSampleset()
            TestAction.FAILED_NOT_ENOUGH_EXAMPLES_OF_SPECIFIC_EMAIL_AARD -> presenter.datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard()
            else -> error("Unknown MockCreeerDataset.TestAction: $nextAction")
        }
    }

    override fun setNextAction(action: TestAction) {
        this.nextAction = action
    }

    override fun reset() {
        nextAction = null
        usedSeed = null
    }

    enum class TestAction {
        SUCCESSFULLY_CREATED,
        FAILED_SAMPLESET_TOO_SMALL,
        FAILED_NOT_ENOUGH_EXAMPLES_OF_SPECIFIC_EMAIL_AARD
    }
}