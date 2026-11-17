package io.pieterswillens.poc.cli.adapter.registreer.email

import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.cli.adapter.CliPresenter
import io.pieterswillens.poc.cli.adapter.ExitCode
import io.pieterswillens.poc.vocabulary.EmailId
import org.slf4j.LoggerFactory
import java.io.PrintWriter

internal class RegistreerEmailCliPresenter(
    private val out: PrintWriter,
    private val err: PrintWriter,
    private val bestandNaam: String
) : CliPresenter(), RegistreerEmailPresenter {

    override fun emailRegistered(emailId: EmailId) {
        LOGGER.info("E-mail geregistreerd - emailId: ${emailId.value}")
        out.println("E-mail geregistreerd - emailId: ${emailId.value}")
        exitCode = ExitCode.OK
    }

    override fun emailAlreadyRegistered(emailId: EmailId) {
        LOGGER.info("E-mail is al geregistreerd - emailId: ${emailId.value}")
        err.println("E-mail is al geregistreerd - emailId: ${emailId.value}")
        exitCode = ExitCode.AL_GEREGISTREERD
    }

    override fun emailRegistrationFailed() {
        LOGGER.warn("E-mail kon niet worden verwerkt: $bestandNaam")
        err.println("E-mail kon niet worden verwerkt: $bestandNaam")
        exitCode = ExitCode.MISLUKT
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RegistreerEmailCliPresenter::class.java)
    }
}