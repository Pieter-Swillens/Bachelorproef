package io.pieterswillens.poc.jakarta.mail.adapter

import io.pieterswillens.poc.domain.email.ports.EmailParser
import io.pieterswillens.poc.domain.email.ports.EmailParsingResult
import io.pieterswillens.poc.domain.email.ports.ParsedEmail
import io.pieterswillens.poc.jakarta.mail.adapter.config.JakartaMailParserConfig
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailMessageId
import jakarta.mail.Address
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

class JakartaMailParser(private val config: JakartaMailParserConfig): EmailParser {
    override fun parse(email: ByteArray): EmailParsingResult {
        return try {
            val message = MimeMessage(config.session(), ByteArrayInputStream(email))
            EmailParsingResult.Ok(parsedEmail = message.toParsedEmail())
        } catch (e: Exception) {
            LOGGER.error("Failed to parse email: $e")
            EmailParsingResult.Mislukt
        }
    }

    private fun MimeMessage.toParsedEmail() = ParsedEmail(
        replyTo = replyTo[0].toEmailAdres(),
        from = from[0].toEmailAdres(),
        messageId = EmailMessageId.from(messageID)
    )

    private fun Address.toEmailAdres(): EmailAdres {
        return when (this) {
            is InternetAddress -> EmailAdres.from(this.address)
            else -> error("Unknown address type $this")
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JakartaMailParser::class.java)
    }
}
