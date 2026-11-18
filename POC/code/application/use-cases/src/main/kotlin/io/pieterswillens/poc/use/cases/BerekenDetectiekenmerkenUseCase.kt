package io.pieterswillens.poc.use.cases

import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.email.EmailRepository
import io.pieterswillens.poc.domain.email.events.DetectiekenmerkenBerekend
import io.pieterswillens.poc.domain.email.ports.RegistreerbaarDomeinService
import io.pieterswillens.poc.domain.email.presenter.BerekenDetectiekenmerkenDomainPresenter
import io.pieterswillens.poc.vocabulary.EmailId

class BerekenDetectiekenmerkenUseCase(
    private val registreerbaarDomeinService: RegistreerbaarDomeinService,
    private val emailRepository: EmailRepository,
    private val eventPublisher: EventPublisher
) {
    fun execute(
        command: BerekenDetectiekenmerkenCommand,
        presenter: BerekenDetectiekenmerkenPresenter
    ) {
        emailRepository.get(command.emailId)?.let { email ->
            email.berekenDetectiekenmerken(registreerbaarDomeinService, presenter)
            emailRepository.save(email)
            eventPublisher.publish(DetectiekenmerkenBerekend(email.emailId))
        } ?: presenter.emailNotFound(command.emailId)
    }
}

interface BerekenDetectiekenmerkenPresenter: BerekenDetectiekenmerkenDomainPresenter {
    fun emailNotFound(emailId: EmailId)
}

data class BerekenDetectiekenmerkenCommand(val emailId: EmailId)