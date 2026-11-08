package io.pieterswillens.poc.domain

import io.pieterswillens.poc.vocabulary.EmailId

data class Email(
    val emailId: EmailId,
    val fromHeader: String?,
    val replyToHeader: String?
)