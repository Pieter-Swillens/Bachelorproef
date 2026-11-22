package io.pieterswillens.poc.vocabulary.dataset

import java.util.*

data class DatasetId private constructor(val value: UUID) {
    companion object {
        fun createNew() = DatasetId(UUID.randomUUID())
    }
}