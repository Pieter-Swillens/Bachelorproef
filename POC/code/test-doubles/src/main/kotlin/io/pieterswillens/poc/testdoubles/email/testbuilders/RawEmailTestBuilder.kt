package io.pieterswillens.poc.testdoubles.email.testbuilders

import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.RawEmail
import java.util.*

class RawEmailTestBuilder {

    private var bestandsnaam: String = "email.eml"
    private var emailAard: EmailAard = EmailAard.LEGITIEM

    private var messageId = UUID.randomUUID().toString()
    private var fromEmail = EmailAdres.noOps
    private var replyToEmail = EmailAdres.noOps

    fun with(
        bestandsnaam: String? = null,
        emailAard: EmailAard? = null,
        fromEmail: EmailAdres? = null,
        replyToEmail: EmailAdres? = null,
        messageId: String? = null,
    ): RawEmailTestBuilder {
        bestandsnaam?.let { this.bestandsnaam = it }
        emailAard?.let { this.emailAard = it }
        fromEmail?.let { this.fromEmail = it }
        replyToEmail?.let { this.replyToEmail = it }
        messageId?.let { this.messageId = it }

        return this
    }

    fun build(): RawEmail =
        RawEmail(
            bestandsnaam = bestandsnaam,
            emailAard = emailAard,
            content = EMAIL_TEMPLATE
                .replace(FROM_EMAIL_PLACEHOLDER, fromEmail.value)
                .replace(REPLY_TO_EMAIL_PLACEHOLDER, replyToEmail.value)
                .replace(MESSAGE_ID_PLACEHOLDER, messageId)
                .trimIndent()
                .toByteArray()
        )

    companion object {
        private const val FROM_EMAIL_PLACEHOLDER = "{FROM_EMAIL}"
        private const val REPLY_TO_EMAIL_PLACEHOLDER = "{REPLY_TO_EMAIL}"
        private const val MESSAGE_ID_PLACEHOLDER = "{MESSAGE_ID}"

        private const val EMAIL_TEMPLATE = """
            From: $FROM_EMAIL_PLACEHOLDER
            Reply-To: $REPLY_TO_EMAIL_PLACEHOLDER
            Message-Id: $MESSAGE_ID_PLACEHOLDER
            
            Dit is een test builder email
        """
    }

}