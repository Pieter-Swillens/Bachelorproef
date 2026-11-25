package io.pieterswillens.poc.testdoubles.dataset

import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId

object TestSampleSetGenerator {

    fun generateSamples(
        amount: Int,
        aard: EmailAard? = null
    ): Set<Sample> {
        return (1..amount).map {
            Sample(
                emailId = EmailId.createNew(),
                bestandsnaam = "irrelevant",
                aard = aard ?: EmailAard.entries.random()
            )
        }.toSet()
    }

}