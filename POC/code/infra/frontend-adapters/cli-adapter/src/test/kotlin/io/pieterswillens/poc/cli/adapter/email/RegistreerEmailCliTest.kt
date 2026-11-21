package io.pieterswillens.poc.cli.adapter.email

import io.pieterswillens.poc.cli.adapter.config.CliTest
import io.pieterswillens.poc.cli.adapter.mocks.MockRegistreerEmail.TestAction
import io.pieterswillens.poc.testdoubles.email.testbuilders.RawEmailTestBuilder
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.RawEmail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.writeBytes

@DisplayName("Given a CLI interface allowing a user to register a new email")
class RegistreerEmailCliTest : CliTest {

    @TempDir
    lateinit var tempDir: Path

    private val command = "registreer"

    @Test
    fun `when successfully registering a legit email then feedback is provided`() {
        val rawEmail = RawEmailTestBuilder().with(bestandsnaam = "legitmail.eml").build()
        context.registreerEmail.setNextAction(TestAction.REGISTERED)

        val result = runCli(command, rawEmail.toPath())

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("E-mail geregistreerd - emailId: ${context.registreerEmail.emailId!!.value}")

        val receivedInput = context.registreerEmail.receivedInput!!
        assertThat(receivedInput.bestandsnaam).isEqualTo(rawEmail.bestandsnaam)
        assertThat(receivedInput.emailAard).isEqualTo(EmailAard.LEGITIEM)
        assertThat(receivedInput.content).isEqualTo(rawEmail.content)
    }

    @Test
    fun `when successfully registering a phishing email then feedback is provided`() {
        val rawEmail = RawEmailTestBuilder().with(bestandsnaam = "phishingmail.eml").build()
        context.registreerEmail.setNextAction(TestAction.REGISTERED)

        val result = runCli(command, rawEmail.toPath())

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("E-mail geregistreerd - emailId: ${context.registreerEmail.emailId!!.value}")
        assertThat(context.registreerEmail.receivedInput!!.bestandsnaam).isEqualTo(rawEmail.bestandsnaam)
        assertThat(context.registreerEmail.receivedInput!!.emailAard).isEqualTo(EmailAard.PHISHING)
        assertThat(context.registreerEmail.receivedInput!!.content).isEqualTo(rawEmail.content)
    }

    @Test
    fun `when the email is already registered then feedback is provided`() {
        context.registreerEmail.setNextAction(TestAction.ALREADY_REGISTERED)

        val result = runCli(command, padNaarEenBestand())

        assertThat(result.exitCode).isEqualTo(2)
        assertThat(result.stderr).isEqualTo("E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailId!!.value}")
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

    @ParameterizedTest
    @ValueSource(strings = ["pdf", "docx", "xlsx", "png", "mp4", "zip"])
    fun `when the file has an unsupported extension then the use case is not called`(extension: String) {
        val result = runCli("registreer", padNaarEenBestand(extension = extension))

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("$extension wordt niet ondersteund.")
    }

    @Test
    fun `when the emailAard cannot be determined`() {
        val result = runCli("registreer", padNaarEenBestand(prefix = "irrelevantprefix"))

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("Label kon niet worden toegekend omdat het pad 'legit' of 'phishing' moet bevatten")
    }

    private fun padNaarEenBestand(
        prefix: String = "legit",
        extension: String = "eml"
    ) = RawEmailTestBuilder().with(bestandsnaam = "${prefix}mail.$extension").build().toPath()

    private fun RawEmail.toPath(): String {
        val bestand = tempDir.resolve(this.bestandsnaam)
        bestand.writeBytes(this.content)
        return bestand.toString()
    }
}