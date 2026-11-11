package io.pieterswillens.poc.domain

import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

class Email private constructor(
    val emailId: EmailId,
    val fromEmail: EmailAdres,
    val replyToEmail: EmailAdres,
    val emailMessageId: EmailMessageId
) {
    companion object {
        fun registreer(
            parsedEmail: ParsedEmail,
            emailUniquenessValidator: EmailUniquenessValidator,
            presenter: RegistreerEmailDomainPresenter
        ): Email? {
            val messageId = EmailMessageId.from(parsedEmail.messageId)
            return when (val uniquenessValidationResult = emailUniquenessValidator.validate(messageId)) {
                is EmailUniquenessValidationResult.BestaatAl -> {
                    presenter.emailAlreadyRegistered(uniquenessValidationResult.emailId)
                    null
                }

                EmailUniquenessValidationResult.BestaatNogNiet -> Email(
                    emailId = EmailId.createNew(),
                    fromEmail = EmailAdres.from(parsedEmail.from),
                    replyToEmail = EmailAdres.from(parsedEmail.replyTo),
                    emailMessageId = EmailMessageId.from(parsedEmail.messageId)
                )

            }
        }

        fun fromSnapshot(snapshot: EmailSnapshot): Email {
            return Email(
                emailId = snapshot.emailId,
                fromEmail = snapshot.fromEmail,
                replyToEmail = snapshot.replyToEmail,
                emailMessageId = snapshot.emailMessageId
            )
        }
    }
}