package io.pieterswillens.poc.domain.risicomodel

import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkId
import io.pieterswillens.poc.domain.email.detectiekenmerken.ExtractieResult
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.dataset.DatasetId

interface GeefTrainingsDatasetService {
    fun geefTrainingsSet(datasetId: DatasetId): GeefTrainingsDatasetResponse
}

sealed interface GeefTrainingsDatasetResponse {
    data class Ok(val trainingsData: Set<TainingsData>): GeefTrainingsDatasetResponse
    data object DatasetNotFound : GeefTrainingsDatasetResponse
    data object DetectiekenmerkenNogNietBerekend : GeefTrainingsDatasetResponse
}

data class TainingsData(
    val emailId: EmailId,
    val emailAard: EmailAard,
    val kenmerkenprofiel: Map<DetectiekenmerkId, ExtractieResult>
)
