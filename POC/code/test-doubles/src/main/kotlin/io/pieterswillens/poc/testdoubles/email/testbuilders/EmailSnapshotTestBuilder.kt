package io.pieterswillens.poc.testdoubles.email.testbuilders

import io.pieterswillens.poc.domain.email.EmailSnapshot
import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

class EmailSnapshotTestBuilder {

    private var emailId = EmailId.createNew()
    private var fromEmail = EmailAdres.noOps
    private var replyToEmail = EmailAdres.noOps
    private var returnPathEmail = EmailAdres.noOps
    private var emailMessageId = EmailMessageId.random()
    private var status: EmailStatus = EmailStatus.Geregistreerd

    fun with(
        emailId: EmailId? = null,
        fromEmail: EmailAdres? = null,
        replyToEmail: EmailAdres? = null,
        returnPathEmail: EmailAdres? = null,
        emailMessageId: EmailMessageId? = null,
        status: EmailStatus? =  null
    ): EmailSnapshotTestBuilder {
        emailId?.let { this.emailId = it }
        fromEmail?.let { this.fromEmail = it }
        replyToEmail?.let { this.replyToEmail = it }
        returnPathEmail?.let { this.returnPathEmail = it }
        emailMessageId?.let { this.emailMessageId = it }
        status?.let { this.status = it }

        return this
    }

    fun build(): EmailSnapshot {
        return EmailSnapshot(
            emailId = emailId,
            fromEmail = fromEmail,
            replyToEmail = replyToEmail,
            returnPathEmail = returnPathEmail,
            emailMessageId = emailMessageId,
            status = status
        )
    }
}