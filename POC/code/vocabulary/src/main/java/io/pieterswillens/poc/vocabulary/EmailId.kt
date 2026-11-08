package io.pieterswillens.poc.vocabulary

import java.util.*

data class EmailId private constructor(val value: UUID) {
    companion object {
        fun createNew() = EmailId(UUID.randomUUID())
    }
}