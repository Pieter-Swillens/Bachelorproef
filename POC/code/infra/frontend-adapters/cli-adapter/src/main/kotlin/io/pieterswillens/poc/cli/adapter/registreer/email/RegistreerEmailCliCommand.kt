package io.pieterswillens.poc.cli.adapter.registreer.email

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.cli.adapter.ExitCode
import picocli.CommandLine.*
import picocli.CommandLine.Model.CommandSpec
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(
    name = "registreer",
    description = ["Registreert een e-mail vanuit een .eml-bestand"],
    mixinStandardHelpOptions = true,
    exitCodeOnInvalidInput = ExitCode.GEBRUIK,
)
class RegistreerEmailCliCommand(private val registreerEmail: RegistreerEmail) : Callable<Int> {

    @Spec
    private lateinit var spec: CommandSpec

    @Parameters(
        index = "0",
        paramLabel = "<pad>",
        description = ["Pad naar het .eml-bestand, of met --bulk naar een map"]
    )
    private lateinit var path: Path

    @Option(
        names = ["-b", "--bulk"],
        description = ["Registreer alle .eml-bestanden in de opgegeven map (inclusief submappen)"]
    )
    private var bulk: Boolean = false

    override fun call() = RegistreerEmailDelegate(
        registreerEmail = registreerEmail,
        path = path,
        bulk = bulk,
        commandLine = spec.commandLine()
    ).run()
}