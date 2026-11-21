package io.pieterswillens.poc.use.cases.dataset

import io.pieterswillens.poc.domain.EventPublisher
import io.pieterswillens.poc.domain.Presenter
import io.pieterswillens.poc.domain.dataset.Sample
import io.pieterswillens.poc.domain.dataset.SampleRepository
import io.pieterswillens.poc.domain.dataset.events.SampleOpgeslagen
import io.pieterswillens.poc.use.cases.Command
import io.pieterswillens.poc.use.cases.UseCase
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId

class SlaSampleOpUseCase(
    private val sampleRepository: SampleRepository,
    private val eventPublisher: EventPublisher
): UseCase<SlaSampleOpCommand, SlaSampleOpPresenter> {

    override fun execute(command: SlaSampleOpCommand, presenter: SlaSampleOpPresenter) {
        val opgeslagenSample = sampleRepository.get(command.emailId)
        if (opgeslagenSample != null) {
            presenter.sampleAlreadyExists(command.emailId)
            return
        }

        sampleRepository.save(
            Sample(
                emailId = command.emailId,
                aard = command.aard
            )
        )
        eventPublisher.publish(SampleOpgeslagen(command.emailId))
        presenter.sampleOpgeslagen(command.emailId, command.aard)
    }

}

interface SlaSampleOpPresenter: Presenter {
    fun sampleOpgeslagen(emailId: EmailId, aard: EmailAard)
    fun sampleAlreadyExists(emailId: EmailId)
}

data class SlaSampleOpCommand(val emailId: EmailId, val aard: EmailAard): Command