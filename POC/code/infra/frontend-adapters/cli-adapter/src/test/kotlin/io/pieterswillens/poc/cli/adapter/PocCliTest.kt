package io.pieterswillens.poc.cli.adapter

import io.pieterswillens.poc.cli.adapter.config.CliTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Given a CLI interface for the POC")
class PocCliTest: CliTest {

    @ParameterizedTest
    @ValueSource(strings = ["-h", "--help"])
    fun `when the user requests help then documentation is provided`(command: String) {
        val expectedOutput = javaClass.getResource("/help-output.txt")!!.readText()

        val result = runCli(command)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo(expectedOutput)
    }

    @ParameterizedTest
    @ValueSource(strings = ["--version", "-V"])
    fun `when the user requests version then version is provided`(command: String) {
        val result = runCli(command)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("1.0.0")
    }
}