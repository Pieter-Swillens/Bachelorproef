package io.pieterswillens.poc.cli.adapter.registreer.email

import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.cli.adapter.CliPresenter
import io.pieterswillens.poc.cli.adapter.ExitCode
import io.pieterswillens.poc.vocabulary.EmailId
import java.io.PrintWriter

internal class RegistreerEmailCliPresenter(
    private val out: PrintWriter,
    private val err: PrintWriter,
    private val bestandNaam: String
) : CliPresenter(), RegistreerEmailPresenter {

    override fun emailRegistered(emailId: EmailId) {
        out.println("E-mail geregistreerd - emailId: ${emailId.value}")
        exitCode = ExitCode.OK
    }

    override fun emailAlreadyRegistered(emailId: EmailId) {
        err.println("E-mail is al geregistreerd - emailId: ${emailId.value}")
        exitCode = ExitCode.AL_GEREGISTREERD
    }

    override fun emailRegistrationFailed() {
        err.println("E-mail kon niet worden verwerkt: $bestandNaam")
        exitCode = ExitCode.MISLUKT
    }
}