package io.pieterswillens.poc.vocabulary

import java.util.*

data class EmailMessageId private constructor(val value: String) {
    companion object {
        fun from(messageId: String) = EmailMessageId(messageId)
        fun random() = EmailMessageId(UUID.randomUUID().toString())
    }
}