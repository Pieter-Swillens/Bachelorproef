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

        val sample = command.toSample()
        sampleRepository.save(sample)
        eventPublisher.publish(SampleOpgeslagen(sample.emailId))
        presenter.sampleOpgeslagen(sample.emailId, sample.bestandsnaam, sample.aard)
    }

    private fun SlaSampleOpCommand.toSample() = Sample(
        emailId = this.emailId,
        bestandsnaam = this.bestandsnaam,
        aard = this.aard
    )
}

interface SlaSampleOpPresenter: Presenter {
    fun sampleOpgeslagen(emailId: EmailId, bestandsnaam: String, aard: EmailAard)
    fun sampleAlreadyExists(emailId: EmailId)
}

data class SlaSampleOpCommand(
    val emailId: EmailId,
    val bestandsnaam: String,
    val aard: EmailAard
): Command