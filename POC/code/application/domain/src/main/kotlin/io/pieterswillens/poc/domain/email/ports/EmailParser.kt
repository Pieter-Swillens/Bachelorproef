package io.pieterswillens.poc.domain.email.ports

import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailMessageId

fun interface EmailParser {
    fun parse(email: ByteArray): EmailParsingResult
}

sealed interface EmailParsingResult {
    data class Ok(val parsedEmail: ParsedEmail) : EmailParsingResult
    data object Mislukt : EmailParsingResult
}

data class ParsedEmail(
    val replyTo: EmailAdres,
    val from: EmailAdres,
    val returnPath: EmailAdres,
    val messageId: EmailMessageId
)
