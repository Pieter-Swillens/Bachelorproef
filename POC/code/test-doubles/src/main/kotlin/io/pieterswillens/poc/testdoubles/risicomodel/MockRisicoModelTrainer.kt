package io.pieterswillens.poc.testdoubles.risicomodel

import io.pieterswillens.poc.domain.risicomodel.RisicoModelTrainer
import io.pieterswillens.poc.domain.risicomodel.TainingsData

class MockRisicoModelTrainer: RisicoModelTrainer {
    var trainingsData: Set<TainingsData>? = null
        private set

    override fun train(trainingsData: Set<TainingsData>) {
        this.trainingsData = trainingsData
    }
}
