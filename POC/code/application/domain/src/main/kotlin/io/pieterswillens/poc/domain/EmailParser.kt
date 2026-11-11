package io.pieterswillens.poc.domain

fun interface EmailParser {
    fun parse(email: ByteArray): EmailParsingResult
}

sealed interface EmailParsingResult {
    data class Ok(val parsedEmail: ParsedEmail) : EmailParsingResult
    data object Mislukt : EmailParsingResult
}

data class ParsedEmail(
    val replyTo: String,
    val from: String,
    val messageId: String,
)
