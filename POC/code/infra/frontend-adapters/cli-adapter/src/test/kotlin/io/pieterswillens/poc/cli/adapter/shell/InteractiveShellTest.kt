package io.pieterswillens.poc.cli.adapter.shell

import io.pieterswillens.poc.cli.adapter.config.CliTest
import io.pieterswillens.poc.cli.adapter.mocks.MockRegistreerEmail
import io.pieterswillens.poc.testdoubles.email.testbuilders.RawEmailTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

@DisplayName("Given a CLI that can be used interactively")
class InteractiveShellTest : CliTest {

    @TempDir
    lateinit var tempDir: Path

    private val rawEmail = RawEmailTestBuilder().build()

    @Test
    fun `when the session starts then a welcome message and a prompt are shown`() {
        val result = runInteractiveCli("exit")

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).contains("typ 'help'").contains("poc>")
        assertThat(result.stderr).isEmpty()
    }

    @Test
    fun `when the input ends then the session ends`() {
        val result = runInteractiveCli()

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stderr).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["exit", "quit"])
    fun `when the user exits then later lines are not executed`(exitCommand: String) {
        val result = runInteractiveCli(exitCommand, "registreer ${quoted(eenBestand())}")

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(context.registreerEmail.receivedInput).isNull()
    }

    @Test
    fun `when multiple commands are entered then all of them run in the same session`() {
        context.registreerEmail.setNextAction(MockRegistreerEmail.TestAction.REGISTERED)
        context.registreerEmail.setNextAction(MockRegistreerEmail.TestAction.ALREADY_REGISTERED)
        val pad = quoted(eenBestand())

        val result = runInteractiveCli("registreer $pad", "registreer $pad", "exit")

        assertThat(context.registreerEmail.receivedInputs).hasSize(2)
        assertThat(result.stdout)
            .contains("E-mail geregistreerd - emailId: ${context.registreerEmail.emailIds[0].value}")
        assertThat(result.stderr)
            .contains("E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[1].value}")
    }

    @Test
    fun `when a path contains spaces and is quoted then the file is found`() {
        context.registreerEmail.setNextAction(MockRegistreerEmail.TestAction.REGISTERED)
        val bestand = eenBestand("legit/map met spaties/email.eml")

        val result = runInteractiveCli("registreer \"$bestand\"")

        assertThat(result.stderr).isEmpty()
        assertThat(context.registreerEmail.receivedInput!!.bestandsnaam).isEqualTo(rawEmail.bestandsnaam)
        assertThat(context.registreerEmail.receivedInput!!.emailAard).isEqualTo(rawEmail.emailAard)
        assertThat(context.registreerEmail.receivedInput!!.content).isEqualTo(rawEmail.content)
    }

    @Test
    fun `when blank lines are entered then nothing is executed`() {
        val result = runInteractiveCli("", "   ", "exit")

        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).isEmpty()
    }

    @Test
    fun `when a command fails then the session continues`() {
        context.registreerEmail.setNextAction(MockRegistreerEmail.TestAction.REGISTERED)

        val result = runInteractiveCli("bestaat-niet", "registreer ${quoted(eenBestand())}", "exit")

        assertThat(result.stderr).contains("bestaat-niet")
        assertThat(result.stdout).contains("E-mail geregistreerd")
    }

    @Test
    fun `when help is requested then the available commands are listed`() {
        val result = runInteractiveCli("help", "exit")

        assertThat(result.stdout).contains("registreer")
        assertThat(context.registreerEmail.receivedInput).isNull()
    }

    @Test
    fun `when a bulk command is followed by a single file command then options do not leak between commands`() {
        context.registreerEmail.setNextAction(MockRegistreerEmail.TestAction.REGISTERED)
        context.registreerEmail.setNextAction(MockRegistreerEmail.TestAction.REGISTERED)
        val map = tempDir.resolve("legit/bulk")
        map.createDirectories()
        map.resolve("a.eml").writeBytes(rawEmail.content)

        val result = runInteractiveCli(
            "registreer --bulk ${quoted(map)}",
            "registreer ${quoted(eenBestand("legit.los.eml"))}",
            "exit"
        )

        assertThat(result.stderr).isEmpty()
        assertThat(context.registreerEmail.receivedInputs).hasSize(2)
    }

    private fun eenBestand(naam: String = "legitmail.eml"): Path {
        val map = tempDir.resolve(naam)
        map.parent.createDirectories()
        map.writeBytes(rawEmail.content)
        return map
    }

    private fun quoted(pad: Path) = "\"$pad\""
}