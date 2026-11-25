package io.pieterswillens.poc.testdoubles.dataset

import io.pieterswillens.poc.domain.dataset.DatasetRepository
import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkId
import io.pieterswillens.poc.domain.email.detectiekenmerken.ExtractieResult
import io.pieterswillens.poc.domain.risicomodel.GeefTrainingsDatasetResponse
import io.pieterswillens.poc.domain.risicomodel.GeefTrainingsDatasetService
import io.pieterswillens.poc.domain.risicomodel.TainingsData
import io.pieterswillens.poc.vocabulary.dataset.DatasetId

class MockGeefTrainingsDatasetService(
    private val datasetRepository: DatasetRepository,
): GeefTrainingsDatasetService {
    var detectiekenmerkenNogNietBerekend = false

    override fun geefTrainingsSet(datasetId: DatasetId): GeefTrainingsDatasetResponse {
        val dataset = datasetRepository.get(datasetId)

        if (detectiekenmerkenNogNietBerekend) {
            return GeefTrainingsDatasetResponse.DetectiekenmerkenNogNietBerekend
        }

        return if (dataset == null) {
            GeefTrainingsDatasetResponse.DatasetNotFound
        } else {
            GeefTrainingsDatasetResponse.Ok(dataset.trainingSet.map { it.toTrainingsData() }.toSet())
        }
    }

    private fun Sample.toTrainingsData() = TainingsData(
        emailId = emailId,
        emailAard = aard,
        kenmerkenprofiel = mapOf(DetectiekenmerkId.A1_FROM_WIJKT_AF_VAN_REPLY_TO to ExtractieResult.NietAfwijkend)
    )
}