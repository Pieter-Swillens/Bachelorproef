package io.pieterswillens.poc.domain.dataset.events

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.vocabulary.dataset.DatasetId

data class DatasetGecreeerd(val datasetId: DatasetId): Event
