package io.pieterswillens.poc.testdoubles

import io.pieterswillens.poc.domain.Email
import io.pieterswillens.poc.domain.EmailRepository
import io.pieterswillens.poc.domain.EmailUniquenessValidationResult
import io.pieterswillens.poc.domain.EmailUniquenessValidator
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