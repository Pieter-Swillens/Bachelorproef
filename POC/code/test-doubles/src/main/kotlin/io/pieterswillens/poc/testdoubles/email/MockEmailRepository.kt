package io.pieterswillens.poc.testdoubles.email

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailRepository
import io.pieterswillens.poc.domain.email.EmailUniquenessValidationResult
import io.pieterswillens.poc.domain.email.EmailUniquenessValidator
import io.pieterswillens.poc.vocabulary.EmailMessageId

class MockEmailRepository: EmailRepository, EmailUniquenessValidator {
    val storedItems = mutableListOf<Email>()

    override fun save(email: Email) {
        this.storedItems.add(email)
    }

    override fun validate(messageId: EmailMessageId): EmailUniquenessValidationResult {
        val storedEmail = this.storedItems.firstOrNull { it.emailMessageId == messageId }

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