package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.domain.email.detectiekenmerken.Detectiekenmerken
import io.pieterswillens.poc.domain.email.ports.EmailUniquenessValidationResult
import io.pieterswillens.poc.domain.email.ports.EmailUniquenessValidator
import io.pieterswillens.poc.domain.email.ports.ParsedEmail
import io.pieterswillens.poc.domain.email.presenter.BerekenDetectiekenmerkenDomainPresenter
import io.pieterswillens.poc.domain.email.presenter.RegistreerEmailDomainPresenter
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId

class Email private constructor(
    val emailId: EmailId,
    val fromEmail: EmailAdres,
    val replyToEmail: EmailAdres,
    val returnPath: EmailAdres,
    val emailMessageId: EmailMessageId,
    status: EmailStatus
) {

    var status: EmailStatus = status
        private set

    fun berekenDetectiekenmerken(
        detectiekenmerken: Detectiekenmerken,
        presenter: BerekenDetectiekenmerkenDomainPresenter,
    ) {
        status = EmailStatus.DetectiekenmerkenBerekend(
            kenmerkenprofiel = detectiekenmerken.geefKenmerkenProfiel(this)
        )
        presenter.detectieKenmerkenSuccesvolBerekend(emailId)
    }

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
                    returnPath = parsedEmail.returnPath,
                    emailMessageId = parsedEmail.messageId,
                    status = EmailStatus.Geregistreerd
                )

            }
        }

        fun fromSnapshot(snapshot: EmailSnapshot): Email {
            return Email(
                emailId = snapshot.emailId,
                fromEmail = snapshot.fromEmail,
                replyToEmail = snapshot.replyToEmail,
                returnPath = snapshot.returnPathEmail,
                emailMessageId = snapshot.emailMessageId,
                status = snapshot.status
            )
        }
    }
}