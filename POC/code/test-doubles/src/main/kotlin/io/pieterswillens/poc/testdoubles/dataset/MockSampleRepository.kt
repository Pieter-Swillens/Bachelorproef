package io.pieterswillens.poc.testdoubles.dataset

import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.domain.dataset.SampleRepository
import io.pieterswillens.poc.vocabulary.EmailId

class MockSampleRepository: SampleRepository {
    val storedItems = mutableMapOf<EmailId, Sample>()

    override fun save(aggregate: Sample) {
        this.storedItems[aggregate.emailId] = aggregate
    }

    override fun get(id: EmailId) = this.storedItems[id]
}