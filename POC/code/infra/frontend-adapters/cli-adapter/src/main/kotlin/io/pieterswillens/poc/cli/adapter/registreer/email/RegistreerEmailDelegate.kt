package io.pieterswillens.poc.cli.adapter.registreer.email

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.cli.adapter.ExitCode
import io.pieterswillens.poc.vocabulary.EmailAard
import picocli.CommandLine
import picocli.CommandLine.ParameterException
import java.nio.file.Path
import kotlin.io.path.*

class RegistreerEmailDelegate(
    private val registreerEmail: RegistreerEmail,
    private val path: Path,
    private val bulk: Boolean,
    private val commandLine: CommandLine
) {
    fun run(): Int {
        return when {
            path.isRegularFile() && bulk -> throw ParameterException(commandLine, "Om meerdere bestanden te imperteren verwijs je naar een geldige folder.")
            path.isDirectory() && !bulk -> throw ParameterException(commandLine, "Om meerdere bestanden te imperteren gebruik je het --bulk argument en verwijs je naar een folder.")
            path.isRegularFile() && path.extension !in ONDERSTEUNDE_EXTENSIES -> throw ParameterException(commandLine, "${path.extension} wordt niet ondersteund.")
            path.isRegularFile() -> registreerFile(path)
            path.isDirectory() -> registerDirectory()
            else -> throw ParameterException(commandLine, "Pad kon niet geresolved worden: $path")
        }
    }

    private fun registerDirectory(): Int {
        val bestanden = path.walk()
            .filter { it.extension in ONDERSTEUNDE_EXTENSIES }
            .sorted()
            .toList()

        val registeredFiles = bestanden.map { registreerFile(it) }
        return reportResult(registeredFiles)
    }

    private fun registreerFile(bestand: Path): Int {
        val presenter = RegistreerEmailCliPresenter(commandLine.out, commandLine.err, path.relativize(bestand).toString())
        registreerEmail.registreerEmail(
            input = RegistreerEmailInput(
                bestandsnaam = bestand.fileName.toString(),
                emailAard = bestand.bepaalEmailAard(),
                content = bestand.readBytes()
            ),
            presenter = presenter
        )
        return presenter.getResponse()
    }

    private fun Path.bepaalEmailAard(): EmailAard {
        return if (this.toString().contains("legit")) {
            EmailAard.LEGITIEM
        } else if (this.toString().contains("phishing")) {
            EmailAard.PHISHING
        } else {
            throw ParameterException(commandLine, "Label kon niet worden toegekend omdat het pad 'legit' of 'phishing' moet bevatten")
        }
    }

    private fun reportResult(registeredFiles: List<Int>): Int {
        commandLine.out.println(
            """
                Resultaat van de bulk import:
                    Aantal nieuwe registraties:     ${registeredFiles.count { it == ExitCode.OK }}
                    Aantal reeds geregistreerd:     ${registeredFiles.count { it == ExitCode.AL_GEREGISTREERD }}
                    Aantal mislukt:                 ${registeredFiles.count { it == ExitCode.MISLUKT }}
            """.trimIndent()
        )

        val containsOk = registeredFiles.any { it == ExitCode.OK }
        return if (containsOk) {
            ExitCode.OK
        } else {
            ExitCode.MISLUKT
        }
    }

    companion object {
        // TODO: temp filtering - should be moved to domain
        private val ONDERSTEUNDE_EXTENSIES = listOf("eml", "txt")
    }
}