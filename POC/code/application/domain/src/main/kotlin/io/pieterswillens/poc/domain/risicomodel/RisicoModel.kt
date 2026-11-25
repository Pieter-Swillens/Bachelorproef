package io.pieterswillens.poc.domain.risicomodel

import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId

data class RisicoModel private constructor(
    val risicoModelId: RisicoModelId,
) {
    companion object {
        fun train(
            trainingsdata: Set<TainingsData>,
            risicoModelTrainer: RisicoModelTrainer
        ): RisicoModel {
            risicoModelTrainer.train(trainingsdata) // TODO: capture and save response
            return RisicoModel(risicoModelId = RisicoModelId.createNew())
        }
    }
}
