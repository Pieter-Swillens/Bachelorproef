package io.pieterswillens.poc.testdoubles.email.testbuilders

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailSnapshot
import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

class GeregistreerdeEmailTestBuilder private constructor(private val snapshot: EmailSnapshot) {
    constructor() : this(DEFAULT_SNAPSHOT)

    fun with(
        emailId: EmailId? = null,
        fromEmail: EmailAdres? = null,
        replyToEmail: EmailAdres? = null,
        returnPathEmail: EmailAdres? = null,
        emailMessageId: EmailMessageId? = null
    ) = GeregistreerdeEmailTestBuilder(
        snapshot = snapshot.copy(
            emailId = emailId ?: snapshot.emailId,
            fromEmail = fromEmail ?: snapshot.fromEmail,
            replyToEmail = replyToEmail ?: snapshot.replyToEmail,
            returnPathEmail = returnPathEmail ?: snapshot.returnPathEmail,
            emailMessageId = emailMessageId ?: snapshot.emailMessageId
        )
    )

    fun detectiekenmerkenBerekend() = DetectiekenmerkenBerekendTestBuilder(snapshot)

    fun build() = Email.fromSnapshot(snapshot)

    companion object {
        private val DEFAULT_SNAPSHOT = EmailSnapshot(
            emailId = EmailId.createNew(),
            fromEmail = EmailAdres.noOps,
            replyToEmail = EmailAdres.noOps,
            returnPathEmail = EmailAdres.noOps,
            emailMessageId = EmailMessageId.random(),
            status = EmailStatus.Geregistreerd
        )

    }
}