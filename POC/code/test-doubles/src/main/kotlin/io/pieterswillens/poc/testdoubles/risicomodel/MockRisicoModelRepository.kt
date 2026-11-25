package io.pieterswillens.poc.testdoubles.risicomodel

import io.pieterswillens.poc.domain.risicomodel.RisicoModel
import io.pieterswillens.poc.domain.risicomodel.RisicoModelRepository
import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId

class MockRisicoModelRepository: RisicoModelRepository {
    val storedItems = mutableMapOf<RisicoModelId, RisicoModel>()

    override fun save(aggregate: RisicoModel) {
        storedItems[aggregate.risicoModelId] = aggregate
    }

    override fun get(id: RisicoModelId): RisicoModel? {
        return storedItems[id]
    }

}