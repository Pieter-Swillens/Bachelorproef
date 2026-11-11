package io.pieterswillens.poc.testdoubles

import io.pieterswillens.poc.domain.EmailParser
import io.pieterswillens.poc.domain.EmailParsingResult
import io.pieterswillens.poc.domain.ParsedEmail
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailMessageId

class StubbedEmailParser : EmailParser {
    private var failOnNext = false

    var usedEmail: ByteArray? = null
        private set
    private var toReturn = ParsedEmail(
        replyTo = EmailAdres.noOps,
        from = EmailAdres.noOps,
        messageId = EmailMessageId.random()
    )

    override fun parse(email: ByteArray): EmailParsingResult {
        this.usedEmail = email
        return if (failOnNext) {
            EmailParsingResult.Mislukt
        } else {
            EmailParsingResult.Ok(toReturn)
        }
    }

    fun setEmailToReturn(parsedEmail: ParsedEmail) {
        toReturn = parsedEmail
    }

    fun failOnNextCall() {
        this.failOnNext = true
    }

}