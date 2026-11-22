package io.pieterswillens.poc.cli.adapter.config

import io.pieterswillens.poc.cli.adapter.CliApplication
import io.pieterswillens.poc.cli.adapter.creeer.dataset.CreeerDatasetCliCommand
import io.pieterswillens.poc.cli.adapter.mocks.MockApplicationApi
import io.pieterswillens.poc.cli.adapter.mocks.MockCreeerDataset
import io.pieterswillens.poc.cli.adapter.mocks.MockRegistreerEmail
import io.pieterswillens.poc.cli.adapter.registreer.email.RegistreerEmailCliCommand
import java.io.PrintStream

class CliApplicationContext {

    val registreerEmail = MockRegistreerEmail()
    val creeerDataset = MockCreeerDataset()

    private val mockApplicationApis = listOf<MockApplicationApi<*>>(
        registreerEmail,
        creeerDataset
    )

    fun cliApplication(out: PrintStream, err: PrintStream) = CliApplication(
        commands = listOf(
            RegistreerEmailCliCommand(registreerEmail),
            CreeerDatasetCliCommand(creeerDataset)
        ),
        out = out,
        err = err,
    )

    internal fun reset() {
        mockApplicationApis.forEach { it.reset() }
    }

    companion object {
        val context = CliApplicationContext()
    }
}