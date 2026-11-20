package io.pieterswillens.poc.use.cases.event.handlers.presenters

import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenPresenter
import io.pieterswillens.poc.vocabulary.EmailId
import org.slf4j.LoggerFactory

object BerekenDetectiekenmerkenLoggingPresenter: BerekenDetectiekenmerkenPresenter {
    override fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId) {
        LOGGER.info("Detectiekenmerken berekend voor email {}", emailId)
    }

    override fun emailNotFound(emailId: EmailId) {
        LOGGER.error("Email met emailId {} niet gevonden bij berekenen detectiekenmerken", emailId)
    }

    private val LOGGER = LoggerFactory.getLogger(BerekenDetectiekenmerkenLoggingPresenter::class.java)
}