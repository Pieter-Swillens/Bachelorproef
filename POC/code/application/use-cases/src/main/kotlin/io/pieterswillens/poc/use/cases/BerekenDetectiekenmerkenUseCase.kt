package io.pieterswillens.poc.use.cases

import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.email.EmailRepository
import io.pieterswillens.poc.domain.email.detectiekenmerken.Detectiekenmerken
import io.pieterswillens.poc.domain.email.events.DetectiekenmerkenBerekend
import io.pieterswillens.poc.domain.email.presenter.BerekenDetectiekenmerkenDomainPresenter
import io.pieterswillens.poc.vocabulary.EmailId

class BerekenDetectiekenmerkenUseCase(
    private val detectiekenmerken: Detectiekenmerken,
    private val emailRepository: EmailRepository,
    private val eventPublisher: EventPublisher,
): UseCase<BerekenDetectiekenmerkenCommand, BerekenDetectiekenmerkenPresenter> {
    override fun execute(
        command: BerekenDetectiekenmerkenCommand,
        presenter: BerekenDetectiekenmerkenPresenter
    ) {
        emailRepository.get(command.emailId)?.let { email ->
            email.berekenDetectiekenmerken(detectiekenmerken, presenter)
            emailRepository.save(email)
            eventPublisher.publish(DetectiekenmerkenBerekend(email.emailId))
        } ?: presenter.emailNotFound(command.emailId)
    }
}

interface BerekenDetectiekenmerkenPresenter: BerekenDetectiekenmerkenDomainPresenter {
    fun emailNotFound(emailId: EmailId)
}

data class BerekenDetectiekenmerkenCommand(val emailId: EmailId): Command