package io.pieterswillens.poc.cli.adapter

import java.io.BufferedReader
import java.io.PrintWriter

internal class InteractiveShell(
    private val input: BufferedReader,
    private val out: PrintWriter,
    private val execute: (List<String>) -> Unit,
    private val printHelp: () -> Unit,
) {
    fun run() {
        out.println(WELCOME)
        while (true) {
            val line = prompt() ?: return endOfInput()
            if (line in EXIT_COMMANDS) return
            handle(line)
        }
    }

    private fun prompt(): String? {
        out.print(PROMPT)
        out.flush()
        return input.readLine()?.trim()
    }

    private fun handle(line: String) = when {
        line.isEmpty() -> Unit
        line == HELP_COMMAND -> printHelp()
        else -> execute(CommandLineTokenizer.tokenize(line))
    }

    private fun endOfInput() = out.println()

    private companion object {
        const val PROMPT = "poc> "
        const val WELCOME = "POC - typ 'help' voor de commando's, 'exit' om te stoppen."
        const val HELP_COMMAND = "help"
        val EXIT_COMMANDS = setOf("exit", "quit")
    }
}