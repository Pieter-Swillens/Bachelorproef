package io.pieterswillens.poc.use.cases.email

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailRepository
import io.pieterswillens.poc.domain.email.events.EmailGeregistreerd
import io.pieterswillens.poc.domain.email.ports.EmailParser
import io.pieterswillens.poc.domain.email.ports.EmailParsingResult
import io.pieterswillens.poc.domain.email.ports.EmailUniquenessValidator
import io.pieterswillens.poc.domain.email.presenter.RegistreerEmailDomainPresenter
import io.pieterswillens.poc.use.cases.Command
import io.pieterswillens.poc.use.cases.UseCase
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.RawEmail

class RegistreerEmailUseCase(
    private val emailParser: EmailParser,
    private val emailUniquenessValidator: EmailUniquenessValidator,
    private val emailRepository: EmailRepository,
    private val eventPublisher: EventPublisher
): RegistreerEmail, UseCase<RegistreerEmailCommand, RegistreerEmailPresenter> {

    override fun registreerEmail(input: RegistreerEmailInput, presenter: RegistreerEmailPresenter) {
        execute(
            command = RegistreerEmailCommand(
                RawEmail(
                    bestandsnaam = input.bestandsnaam,
                    emailAard = input.emailAard,
                    content = input.content
                )
            ),
            presenter = presenter,
        )
    }

    override fun execute(command: RegistreerEmailCommand, presenter: RegistreerEmailPresenter) {
        when (val result = emailParser.parse(command.rawEmail.content)) {
            EmailParsingResult.Mislukt -> {
                presenter.emailRegistrationFailed()
                return
            }

            is EmailParsingResult.Ok -> {
                Email.registreer(
                    parsedEmail = result.parsedEmail,
                    emailUniquenessValidator = emailUniquenessValidator,
                    presenter = presenter.asDomainPresenter()
                )?.let {
                    emailRepository.save(it)
                    eventPublisher.publish(
                        EmailGeregistreerd(
                            emailId = it.emailId,
                            bestandsnaam = command.rawEmail.bestandsnaam,
                            aard = command.rawEmail.emailAard,
                        ),
                    )
                    presenter.emailRegistered(it.emailId)
                }
            }
        }
    }

    private fun RegistreerEmailPresenter.asDomainPresenter() = object : RegistreerEmailDomainPresenter {
        override fun emailAlreadyRegistered(emailId: EmailId) = this@asDomainPresenter.emailAlreadyRegistered(emailId)
    }
}

data class RegistreerEmailCommand(val rawEmail: RawEmail): Command
