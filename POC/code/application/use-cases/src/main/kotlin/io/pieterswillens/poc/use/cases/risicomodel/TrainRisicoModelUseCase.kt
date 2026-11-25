package io.pieterswillens.poc.use.cases.risicomodel

import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.risicomodel.*
import io.pieterswillens.poc.domain.risicomodel.events.RisicoModelGetraind
import io.pieterswillens.poc.use.cases.Command
import io.pieterswillens.poc.use.cases.UseCase
import io.pieterswillens.poc.vocabulary.Presenter
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId

class TrainRisicoModelUseCase(
    private val risicoModelTrainer: RisicoModelTrainer,
    private val risicoModelRepository: RisicoModelRepository,
    private val geefTrainingsDatasetService: GeefTrainingsDatasetService,
    private val eventPublisher: EventPublisher
): UseCase<TrainRisicoModelCommand, TrainRisicoModelPresenter> {
    override fun execute(
        command: TrainRisicoModelCommand,
        presenter: TrainRisicoModelPresenter
    ) {
        when (val geefTrainingsDataResponse = geefTrainingsDatasetService.geefTrainingsSet(command.datasetId)) {
            GeefTrainingsDatasetResponse.DatasetNotFound -> {
                presenter.datasetNotFound(command.datasetId)
                return
            }
            GeefTrainingsDatasetResponse.DetectiekenmerkenNogNietBerekend -> {
                presenter.datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijn(command.datasetId)
                return
            }
            is GeefTrainingsDatasetResponse.Ok -> {
                val risicomodel = RisicoModel.train(geefTrainingsDataResponse.trainingsData, risicoModelTrainer)
                risicoModelRepository.save(risicomodel)
                eventPublisher.publish(RisicoModelGetraind(risicomodel.risicoModelId))
                presenter.risicoModelGetraind(risicomodel.risicoModelId)
            }
        }

    }

}

data class TrainRisicoModelCommand(val datasetId: DatasetId): Command

interface TrainRisicoModelPresenter: Presenter {
    fun risicoModelGetraind(risicoModelId: RisicoModelId)
    fun datasetNotFound(datasetId: DatasetId)
    fun datasetBevatEmailsWaarvanDetectiekenmerkenNogNietBerekendZijn(datasetId: DatasetId)
}
