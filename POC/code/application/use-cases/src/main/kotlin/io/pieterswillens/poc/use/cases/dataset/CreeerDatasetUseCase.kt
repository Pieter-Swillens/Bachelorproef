package io.pieterswillens.poc.use.cases.dataset

import io.pieterswillens.poc.api.CreeerDataset
import io.pieterswillens.poc.api.CreeerDatasetPresenter
import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.dataset.Dataset
import io.pieterswillens.poc.domain.dataset.DatasetRepository
import io.pieterswillens.poc.domain.dataset.GeefSamplesService
import io.pieterswillens.poc.domain.dataset.events.DatasetGecreeerd
import io.pieterswillens.poc.domain.dataset.presenter.CreeerDatasetDomainPresenter
import io.pieterswillens.poc.use.cases.Command
import io.pieterswillens.poc.use.cases.UseCase
import io.pieterswillens.poc.vocabulary.dataset.Seed

class CreeerDatasetUseCase(
    private val geefSamplesService: GeefSamplesService,
    private val datasetRepository: DatasetRepository,
    private val eventPublisher: EventPublisher
): CreeerDataset, UseCase<CreeerDatasetCommand, CreeerDatasetPresenter> {

    override fun creeerDataset(
        seed: Seed,
        presenter: CreeerDatasetPresenter
    ) = execute(command = CreeerDatasetCommand(seed), presenter = presenter)

    override fun execute(
        command: CreeerDatasetCommand,
        presenter: CreeerDatasetPresenter
    ) {
        Dataset.creeer(command.seed, geefSamplesService, presenter.asDomainPresenter())?.let { dataset ->
            datasetRepository.save(dataset)
            eventPublisher.publish(DatasetGecreeerd(dataset.datasetId))
            presenter.datasetGecreeerd(dataset.datasetId, command.seed)
        }
    }

    private fun CreeerDatasetPresenter.asDomainPresenter() = object : CreeerDatasetDomainPresenter {
        override fun datasetCreatieMisluktDoorTeKleineSampleset() = this@asDomainPresenter.datasetCreatieMisluktDoorTeKleineSampleset()
        override fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard() = this@asDomainPresenter.datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard()
    }
}

data class CreeerDatasetCommand(val seed: Seed): Command
