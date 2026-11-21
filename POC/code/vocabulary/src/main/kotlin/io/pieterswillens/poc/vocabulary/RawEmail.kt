package io.pieterswillens.poc.vocabulary

data class RawEmail(
    val bestandsnaam: String,
    val emailAard: EmailAard,
    val content: ByteArray
)