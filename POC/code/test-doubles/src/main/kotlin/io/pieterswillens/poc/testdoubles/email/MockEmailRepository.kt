package io.pieterswillens.poc.testdoubles.email

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailRepository
import io.pieterswillens.poc.domain.email.ports.EmailUniquenessValidationResult
import io.pieterswillens.poc.domain.email.ports.EmailUniquenessValidator
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

class MockEmailRepository: EmailRepository, EmailUniquenessValidator {
    val storedItems = mutableMapOf<EmailId, Email>()

    override fun save(email: Email) {
        this.storedItems[email.emailId] = email
    }

    override fun get(emailId: EmailId): Email? {
        return storedItems[emailId]
    }

    override fun validate(messageId: EmailMessageId): EmailUniquenessValidationResult {
        val storedEmail = this.storedItems
            .values
            .firstOrNull { it.emailMessageId == messageId }

        return if (storedEmail == null) {
            EmailUniquenessValidationResult.BestaatNogNiet
        } else {
            EmailUniquenessValidationResult.BestaatAl(storedEmail.emailId)
        }
    }

    fun clear() {
        this.storedItems.clear()
    }

}