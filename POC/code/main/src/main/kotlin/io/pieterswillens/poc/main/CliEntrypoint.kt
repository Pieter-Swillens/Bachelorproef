package io.pieterswillens.poc.main

import io.pieterswillens.poc.cli.adapter.CliApplication
import io.pieterswillens.poc.cli.adapter.registreer.email.RegistreerEmailCliCommand
import io.pieterswillens.poc.main.dependencies.Dependencies
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    val cliCommands = listOf(
        RegistreerEmailCliCommand(Dependencies.registreerEmail),
    )

    val cli = CliApplication(commands = cliCommands)

    exitProcess(
        if (args.isEmpty()) cli.runInteractive()
        else cli.run(args)
    )
}