package io.pieterswillens.poc.domain.risicomodel

interface RisicoModelTrainer {
    fun train(trainingsData: Set<TainingsData>) // TODO: define a return type
}
