package io.pieterswillens.poc.cli.adapter.email

import io.pieterswillens.poc.cli.adapter.config.CliTest
import io.pieterswillens.poc.cli.adapter.mocks.MockRegistreerEmail.TestAction
import io.pieterswillens.poc.testdoubles.email.RawEmailTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeBytes

@DisplayName("Given a CLI interface allowing a user to register a new email")
class RegistreerEmailCliTest : CliTest {

    @TempDir
    lateinit var tempDir: Path

    private val command = "registreer"

    @Test
    fun `when successfully registering an email then feedback is provided`() {
        context.registreerEmail.setNextAction(TestAction.REGISTERED)

        val result = runCli(command, padNaarEenBestand())

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("E-mail geregistreerd - emailId: ${context.registreerEmail.emailId!!.value.toString()}")
        assertThat(context.registreerEmail.receivedInput!!.content).isEqualTo(rawEmail.content)
    }

    @Test
    fun `when the email is already registered then feedback is provided`() {
        context.registreerEmail.setNextAction(TestAction.ALREADY_REGISTERED)

        val result = runCli(command, padNaarEenBestand())

        assertThat(result.exitCode).isEqualTo(2)
        assertThat(result.stderr).isEqualTo("E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailId!!.value.toString()}")
    }

    @Test
    fun `when the email cannot be processed then feedback is provided`() {
        context.registreerEmail.setNextAction(TestAction.REGISTRATION_FAILED)

        val result = runCli(command, padNaarEenBestand())

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(result.stderr).contains("E-mail kon niet worden verwerkt")
    }

    @Test
    fun `when the file does not exist then the use case is not called`() {
        val result = runCli(command, tempDir.resolve("bestaat-niet.eml").toString())

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("Pad kon niet geresolved worden")
    }

    @Test
    fun `when the file argument is missing then the use case is not called`() {
        val result = runCli(command)

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).isNotEmpty()
    }

    @Test
    fun `when the command is unknown then the use case is not called`() {
        val result = runCli("bestaat-niet")

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(result.stderr).contains("bestaat-niet")
    }

    private val rawEmail = RawEmailTestBuilder().build()
    private fun padNaarEenBestand(): String {
        val bestand = tempDir.resolve("mail.eml")
        bestand.writeBytes(rawEmail.content)
        return bestand.toString()
    }
}