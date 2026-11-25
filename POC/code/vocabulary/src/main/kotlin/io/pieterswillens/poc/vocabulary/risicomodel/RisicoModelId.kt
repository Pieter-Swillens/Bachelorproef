package io.pieterswillens.poc.vocabulary.risicomodel

import java.util.*

data class RisicoModelId private constructor(val value: UUID) {
    companion object {
        fun createNew() = RisicoModelId(UUID.randomUUID())
    }
}