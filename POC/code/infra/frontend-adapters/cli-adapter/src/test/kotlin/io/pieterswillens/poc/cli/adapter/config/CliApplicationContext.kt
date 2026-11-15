package io.pieterswillens.poc.cli.adapter.config

import io.pieterswillens.poc.cli.adapter.mocks.MockApplicationApi
import io.pieterswillens.poc.cli.adapter.mocks.MockRegistreerEmail
import io.pieterswillens.poc.cli.adapter.registreer.mail.RegistreerEmailCliCommand
import java.io.PrintStream

class CliApplicationContext {

    val registreerEmail = MockRegistreerEmail()

    private val mockApplicationApis = listOf<MockApplicationApi<*>>(
        registreerEmail,
    )

    fun cliApplication(out: PrintStream, err: PrintStream) = CliApplication(
        commands = listOf(
            RegistreerEmailCliCommand(registreerEmail),
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