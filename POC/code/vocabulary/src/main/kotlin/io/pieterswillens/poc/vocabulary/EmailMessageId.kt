package io.pieterswillens.poc.vocabulary

data class EmailMessageId private constructor(val value: String) {
    companion object {
        fun from(messageId: String) = EmailMessageId(messageId)
    }
}