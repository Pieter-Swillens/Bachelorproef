package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.domain.dataset.presenter.CreeerDatasetDomainPresenter
import io.pieterswillens.poc.testdoubles.dataset.MockSampleRepository
import io.pieterswillens.poc.vocabulary.dataset.Seed
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given a seeding mechanism to seed a dataset")
class DatasetSeedingTest {

    private val sampleService = MockSampleRepository()
    private val presenter = MockPresenter()

    @BeforeEach
    fun setUp() {
        sampleService.fill(size = 100)
    }

    @Nested
    internal inner class `when creating a dataset from exactly the same sampleset multiple times` {

        @Test
        fun `then the result is the same over multiple creations`() {
            val firstDataset = Dataset.creeer(Seed.DEFAULT, sampleService, presenter)
            val secondDataset = Dataset.creeer(Seed.DEFAULT, sampleService, presenter)

            assertThat(firstDataset).usingRecursiveComparison(comparisonConfig).isEqualTo(secondDataset)
        }
    }

    @Nested
    internal inner class `when creating a dataset from samplesets containing the same data but different order multiple times`{

        @Test
        fun `then the result is the same over multiple creations`() {
            val firstDataset = Dataset.creeer(Seed.DEFAULT, sampleService, presenter)

            sampleService.shuffle()
            val secondDataset = Dataset.creeer(Seed.DEFAULT, sampleService, presenter)

            assertThat(firstDataset).usingRecursiveComparison(comparisonConfig).isEqualTo(secondDataset)
        }
    }

    @Nested
    internal inner class `when creating a dataset from exactly the same sampleset but using a different seed`{

        @Test
        fun `then the result differs`() {
            val firstDataset = Dataset.creeer(Seed(16), sampleService, presenter)
            val secondDataset = Dataset.creeer(Seed(512), sampleService, presenter)

            assertThat(firstDataset).usingRecursiveComparison(comparisonConfig).isNotEqualTo(secondDataset)
        }
    }

    @Nested
    internal inner class `when creating a dataset from different samplesets`{

        val anotherSampleService = MockSampleRepository()

        @BeforeEach
        fun setUp() {
            anotherSampleService.fill(size = 25)
        }

        @Test
        fun `then the result differs`() {
            val firstDataset = Dataset.creeer(Seed.DEFAULT, sampleService, presenter)
            val secondDataset = Dataset.creeer(Seed.DEFAULT, anotherSampleService, presenter)

            assertThat(firstDataset).usingRecursiveComparison(comparisonConfig).isNotEqualTo(secondDataset)
        }
    }

    val comparisonConfig = RecursiveComparisonConfiguration.builder()
        .withIgnoredFields("datasetId")
        .build()

    private class MockPresenter : CreeerDatasetDomainPresenter {
        override fun datasetCreatieMisluktDoorTeKleineSampleset() {
            // do nothing
        }

        override fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard() {
            // do nothing
        }

    }
}