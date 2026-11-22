package io.pieterswillens.poc.use.cases.dataset

import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.dataset.Dataset
import io.pieterswillens.poc.domain.dataset.DatasetRepository
import io.pieterswillens.poc.domain.dataset.GeefSamplesService
import io.pieterswillens.poc.domain.dataset.events.DatasetGecreeerd
import io.pieterswillens.poc.domain.dataset.presenter.CreeerDatasetDomainPresenter
import io.pieterswillens.poc.use.cases.Command
import io.pieterswillens.poc.use.cases.UseCase
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed

class CreeerDatasetUseCase(
    private val geefSamplesService: GeefSamplesService,
    private val datasetRepository: DatasetRepository,
    private val eventPublisher: EventPublisher
): UseCase<CreeerDatasetCommand, CreeerDatasetPresenter> {

    override fun execute(
        command: CreeerDatasetCommand,
        presenter: CreeerDatasetPresenter
    ) {
        Dataset.creeer(command.seed, geefSamplesService, presenter)?.let { dataset ->
            datasetRepository.save(dataset)
            eventPublisher.publish(DatasetGecreeerd(dataset.datasetId))
            presenter.datasetGecreeerd(dataset.datasetId, command.seed)
        }
    }

}

interface CreeerDatasetPresenter: CreeerDatasetDomainPresenter {
    fun datasetGecreeerd(datasetId: DatasetId, seed: Seed)
}

data class CreeerDatasetCommand(val seed: Seed): Command
