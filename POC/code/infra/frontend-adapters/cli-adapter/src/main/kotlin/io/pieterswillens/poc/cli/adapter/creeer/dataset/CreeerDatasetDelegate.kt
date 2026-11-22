package io.pieterswillens.poc.cli.adapter.creeer.dataset

import io.pieterswillens.poc.api.CreeerDataset
import io.pieterswillens.poc.vocabulary.dataset.Seed
import picocli.CommandLine

class CreeerDatasetDelegate(
    private val creeerDataset: CreeerDataset,
    private val seed: String?,
    private val commandLine: CommandLine
) {
    fun run(): Int {
        val presenter = CreeerDatasetCliPresenter(commandLine.out, commandLine.err)
        creeerDataset.creeerDataset(seed.toVocabulary(), presenter)
        return presenter.getResponse()
    }

    private fun String?.toVocabulary(): Seed {
        if (this == null) {
            return Seed.DEFAULT
        }

        val parsedInt = this.toIntOrNull()
        if (parsedInt == null) {
            throw CommandLine.ParameterException(commandLine, "$this is een ongeldige seed. De seed moet een geheel getal zijn.")
        }

        return Seed(parsedInt)
    }
}