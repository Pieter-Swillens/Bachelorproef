package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.acceptance.tests.presenters.CreeerDatasetTestPresenter
import io.pieterswillens.poc.acceptance.tests.traits.DatasetTraits
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

interface DatasetTest: DatasetTraits {
    @Test
    fun `when testing the flow of a dataset`() {
        givenAValidSampleSet()
        val datasetId = whenCreeerDataset()
        thenDatasetShouldBeCreatedAndSaved(datasetId)
    }

    fun whenCreeerDataset(): DatasetId {
        val presenter = CreeerDatasetTestPresenter()
        context.creeerDataset.creeerDataset(
            seed = Seed.DEFAULT,
            presenter = presenter,
        )

        return presenter.presentedDatasetId
    }

    fun thenDatasetShouldBeCreatedAndSaved(datasetId: DatasetId) {
        val savedDataset = context.datasetRepository.get(datasetId)

        assertThat(savedDataset).isNotNull()
    }
}