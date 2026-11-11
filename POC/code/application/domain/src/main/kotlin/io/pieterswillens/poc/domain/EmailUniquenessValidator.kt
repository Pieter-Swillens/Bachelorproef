package io.pieterswillens.poc.domain

import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

fun interface EmailUniquenessValidator {
    fun validate(messageId: EmailMessageId): EmailUniquenessValidationResult
}

sealed interface EmailUniquenessValidationResult {
    data object BestaatNogNiet: EmailUniquenessValidationResult
    data class BestaatAl(val emailId: EmailId): EmailUniquenessValidationResult
}
