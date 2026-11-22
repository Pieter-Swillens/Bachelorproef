package io.pieterswillens.poc.testdoubles.dataset

import io.pieterswillens.poc.domain.dataset.Dataset
import io.pieterswillens.poc.domain.dataset.DatasetRepository
import io.pieterswillens.poc.vocabulary.dataset.DatasetId

class MockDatasetRepository: DatasetRepository {
    val storedItems = mutableMapOf<DatasetId, Dataset>()

    override fun save(aggregate: Dataset) {
        storedItems[aggregate.datasetId] = aggregate
    }

    override fun get(id: DatasetId): Dataset? {
        return storedItems[id]
    }

    fun clear() {
        this.storedItems.clear()
    }

}