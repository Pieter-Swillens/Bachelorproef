package io.pieterswillens.poc.testdoubles.dataset

import io.pieterswillens.poc.domain.dataset.GeefSamplesService
import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.domain.dataset.SampleRepository
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId

class MockSampleRepository: SampleRepository, GeefSamplesService {
    val storedItems = mutableMapOf<EmailId, Sample>()

    override fun save(aggregate: Sample) {
        this.storedItems[aggregate.emailId] = aggregate
    }

    override fun get(id: EmailId) = this.storedItems[id]

    override fun geefSamples() = storedItems.values.toSet()

    fun fill(size: Int, emailAard: EmailAard? = null) {
        for (index in 1..size) {
            val emailId = EmailId.createNew()
            this.storedItems[emailId] = Sample(
                emailId = emailId,
                bestandsnaam = "email-$index.eml",
                aard = emailAard ?: EmailAard.entries.random()
            )
        }
    }

    fun shuffle() {
        val shuffledEntries = storedItems.entries.shuffled()
        storedItems.clear()
        shuffledEntries.forEach { storedItems[it.key] = it.value }
    }
}