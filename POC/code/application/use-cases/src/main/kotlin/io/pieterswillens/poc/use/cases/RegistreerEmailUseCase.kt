package io.pieterswillens.poc.use.cases

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.domain.*
import io.pieterswillens.poc.vocabulary.RawEmail

class RegistreerEmailUseCase(
    private val emailParser: EmailParser,
    private val emailUniquenessValidator: EmailUniquenessValidator,
    private val emailRepository: EmailRepository
): RegistreerEmail {

    override fun registreerEmail(input: RegistreerEmailInput, presenter: RegistreerEmailPresenter) {
        execute(
            registreerEmailCommand = RegistreerEmailCommand(RawEmail(input.content)),
            presenter = presenter,
        )
    }

    fun execute(registreerEmailCommand: RegistreerEmailCommand, presenter: RegistreerEmailPresenter) {
        when (val result = emailParser.parse(registreerEmailCommand.rawEmail.content)) {
            EmailParsingResult.Mislukt -> {
                presenter.emailRegistrationFailed()
                return
            }

            is EmailParsingResult.Ok -> {
                Email.registreer(
                    parsedEmail = result.parsedEmail,
                    emailUniquenessValidator = emailUniquenessValidator,
                    presenter = presenter
                )?.let {
                    emailRepository.save(it)
                    presenter.emailRegistered(it.emailId)
                }
            }
        }
    }
}

data class RegistreerEmailCommand(val rawEmail: RawEmail)
