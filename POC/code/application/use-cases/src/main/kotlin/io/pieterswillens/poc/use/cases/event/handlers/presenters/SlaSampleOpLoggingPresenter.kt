package io.pieterswillens.poc.use.cases.event.handlers.presenters

import io.pieterswillens.poc.use.cases.dataset.SlaSampleOpPresenter
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import org.slf4j.LoggerFactory

object SlaSampleOpLoggingPresenter : SlaSampleOpPresenter {
    override fun sampleOpgeslagen(emailId: EmailId, bestandsnaam: String, aard: EmailAard) {
        LOGGER.info("Sample opgeslagen voor {} met ID {} - aard {}", bestandsnaam, emailId, aard)
    }

    override fun sampleAlreadyExists(emailId: EmailId) {
        LOGGER.error("Sample voor email met emailId {} bestaat al", emailId)
    }

    private val LOGGER = LoggerFactory.getLogger(SlaSampleOpLoggingPresenter::class.java)
}