package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.testdoubles.dataset.TestSampleSetGenerator.generateSamples
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Given dataset creation")
class DatasetTest {

    @Test
    fun `when constructing a dataset with containing at least the minimum amount of samples and examples per EmailAard`() {
        assertThatCode { Dataset.fromSnapshot(snapshot) }.doesNotThrowAnyException()
    }

    @Test
    fun `when constructing a dataset with an empty trainingSet then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    trainingSet = emptySet()
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `when constructing a dataset with an empty evaluatieSet then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    evaluatieSet = emptySet()
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `when constructing a dataset without any samples then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    trainingSet = emptySet(),
                    evaluatieSet = emptySet()
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `when constructing a dataset with only legit samples then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    trainingSet = generateSamples(amount = 8, aard = EmailAard.LEGITIEM),
                    evaluatieSet = generateSamples(amount = 2, aard = EmailAard.LEGITIEM)
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `when constructing a dataset with only one phishing samples then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    trainingSet = generateSamples(amount = 9, aard = EmailAard.LEGITIEM),
                    evaluatieSet = generateSamples(amount = 1, aard = EmailAard.PHISHING)
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `when constructing a dataset with only phishing samples then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    trainingSet = generateSamples(amount = 8, aard = EmailAard.PHISHING),
                    evaluatieSet = generateSamples(amount = 2, aard = EmailAard.PHISHING)
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `when constructing a dataset with only one legit sample then it should not create the dataset`() {
        assertThatThrownBy {
            Dataset.fromSnapshot(
                snapshot.copy(
                    trainingSet = generateSamples(amount = 9, aard = EmailAard.PHISHING),
                    evaluatieSet = generateSamples(amount = 1, aard = EmailAard.LEGITIEM)
                )
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    val snapshot = DatasetSnapshot(
        datasetId = DatasetId.createNew(),
        seed = Seed.DEFAULT,
        trainingSet = generateSamples(amount = 50),
        evaluatieSet = generateSamples(amount = 50)
    )
}