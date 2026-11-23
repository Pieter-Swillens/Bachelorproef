package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

data class EmailSnapshot(
    val emailId: EmailId,
    val fromEmail: EmailAdres,
    val replyToEmail: EmailAdres,
    val returnPathEmail: EmailAdres,
    val emailMessageId: EmailMessageId,
    val status: EmailStatus = EmailStatus.Geregistreerd
)
