package io.pieterswillens.poc.cli.adapter

import picocli.CommandLine
import java.io.InputStream
import java.io.PrintStream
import java.io.PrintWriter

class CliApplication(
    commands: List<Any>,
    out: PrintStream = System.out,
    err: PrintStream = System.err,
) {
    private val outWriter = PrintWriter(out, true)
    private val errWriter = PrintWriter(err, true)
    private val commandLine = createCommandLine(commands)

    private fun createCommandLine(commands: List<Any>): CommandLine {
        val commandLine = CommandLine(PocCli())
        commands.forEach { commandLine.addSubcommand(it) }
        commandLine.setOut(outWriter)
        commandLine.setErr(errWriter)
        return commandLine
    }

    fun run(args: Array<String>): Int {
        val exitCode = commandLine.execute(*args)
        outWriter.flush()
        errWriter.flush()
        return exitCode
    }

    fun runInteractive(input: InputStream = System.`in`): Int {
        InteractiveShell(
            input = input.bufferedReader(),
            out = outWriter,
            execute = { args -> run(args.toTypedArray()) },
            printHelp = { commandLine.usage(outWriter) },
        ).run()
        return ExitCode.OK
    }
}