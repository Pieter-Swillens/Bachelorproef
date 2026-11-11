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
            return when (val uniquenessValidationResult = emailUniquenessValidator.validate(parsedEmail.messageId)) {
                is EmailUniquenessValidationResult.BestaatAl -> {
                    presenter.emailAlreadyRegistered(uniquenessValidationResult.emailId)
                    null
                }

                EmailUniquenessValidationResult.BestaatNogNiet -> Email(
                    emailId = EmailId.createNew(),
                    fromEmail = parsedEmail.from,
                    replyToEmail = parsedEmail.replyTo,
                    emailMessageId = parsedEmail.messageId
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