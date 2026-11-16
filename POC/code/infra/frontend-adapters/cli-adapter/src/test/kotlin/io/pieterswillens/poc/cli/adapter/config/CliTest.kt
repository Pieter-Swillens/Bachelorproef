package io.pieterswillens.poc.cli.adapter.config

import org.junit.jupiter.api.BeforeEach
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

interface CliTest {

    val context: CliApplicationContext
        get() = CliApplicationContext.context

    fun runCli(vararg args: String): CliResult {
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        val exitCode = context
            .cliApplication(PrintStream(out), PrintStream(err))
            .run(arrayOf(*args))

        return CliResult(
            exitCode = exitCode,
            stdout = out.toString().trim(),
            stderr = err.toString().trim()
        )
    }

    fun runInteractiveCli(vararg lines: String): CliResult {
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()
        val exitCode = context
            .cliApplication(PrintStream(out), PrintStream(err))
            .runInteractive(ByteArrayInputStream(lines.joinToString("\n").toByteArray()))

        return CliResult(
            exitCode = exitCode,
            stdout = out.toString().trim(),
            stderr = err.toString().trim()
        )
    }

    @BeforeEach
    fun tearDown() {
        context.reset()
    }
}

data class CliResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
)