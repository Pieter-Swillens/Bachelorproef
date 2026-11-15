package io.pieterswillens.poc.cli.adapter

import picocli.CommandLine.*
import picocli.CommandLine.Model.CommandSpec
import java.util.concurrent.Callable

@Command(
    name = "poc",
    mixinStandardHelpOptions = true,
    exitCodeOnInvalidInput = ExitCode.GEBRUIK,
)
class PocCli : Callable<Int> {

    @Spec
    private lateinit var spec: CommandSpec

    override fun call(): Int {
        throw ParameterException(spec.commandLine(), "Geef een commando op")
    }
}