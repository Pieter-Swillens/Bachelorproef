package io.pieterswillens.poc.cli.adapter.creeer.dataset

import io.pieterswillens.poc.api.CreeerDataset
import io.pieterswillens.poc.cli.adapter.ExitCode
import picocli.CommandLine.*
import picocli.CommandLine.Model.CommandSpec
import java.util.concurrent.Callable

@Command(
    name = "mkdataset",
    description = ["Creëert een dataset waartegen het model getraind kan worden. De dataset is gebaseerd op de reeds geregistreerde e-mails."],
    mixinStandardHelpOptions = true,
    exitCodeOnInvalidInput = ExitCode.GEBRUIK,
)
class CreeerDatasetCliCommand(private val creeerDataset: CreeerDataset): Callable<Int> {

    @Spec
    private lateinit var spec: CommandSpec

    @Option(
        names = ["-s", "--seed"],
        description = ["Seed voor de willekeurige verdeling van de dataset, voor reproduceerbare resultaten."]
    )
    private var seed: String? = null

    override fun call() = CreeerDatasetDelegate(
        creeerDataset = creeerDataset,
        seed = seed,
        commandLine = spec.commandLine()
    ).run()
}