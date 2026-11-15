package io.pieterswillens.poc.cli.adapter

import io.pieterswillens.poc.cli.adapter.config.CliTest
import io.pieterswillens.poc.cli.adapter.mocks.MockRegistreerEmail
import io.pieterswillens.poc.testdoubles.RawEmailTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

@DisplayName("Given a CLI interface allowing a user to register multiple new emails")
class RegistreerEmailsInBulkCliTest: CliTest {

    @TempDir
    lateinit var tempDir: Path

    @ParameterizedTest
    @ValueSource(strings = ["--bulk", "-b"])
    fun `when successfully registering at least one email from multiple emails then OK feedback is provided`(bulkArgument: String) {
        val padNaarMap = setupDirectory(
            mapOf(
                "bestand1.eml" to MockRegistreerEmail.TestAction.REGISTERED,
                "bestand2.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "bestand3.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED,
                "submap/bestand4.eml" to MockRegistreerEmail.TestAction.REGISTERED,
                "submap/bestand5.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "submap/bestand6.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED
            )
        )

        val result = runCli("registreer", bulkArgument, padNaarMap)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("""
            E-mail geregistreerd - emailId: ${context.registreerEmail.emailIds[0].value}
            E-mail geregistreerd - emailId: ${context.registreerEmail.emailIds[3].value}
        """.trimIndent())
        assertThat(result.stderr).isEqualTo("""
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[1].value}
            E-mail kon niet worden verwerkt: bestand3.eml
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[4].value}
            E-mail kon niet worden verwerkt: submap/bestand6.eml
        """.trimIndent())
    }

    @Test
    fun `when all registering multiple emails and all of them fail then MISLUKT feedback is provided`() {
        val padNaarMap = setupDirectory(
            mapOf(
                "bestand2.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "bestand3.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED,
                "submap/bestand5.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "submap/bestand6.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED
            )
        )

        val result = runCli("registreer", "--bulk", padNaarMap)

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(result.stdout).isEmpty()
        assertThat(result.stderr).isEqualTo("""
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[0].value}
            E-mail kon niet worden verwerkt: bestand3.eml
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[2].value}
            E-mail kon niet worden verwerkt: submap/bestand6.eml
        """.trimIndent())
    }

    @Test
    fun `when the directory does not exist then the use case is not called`() {
        val result = runCli("registreer", "--bulk", "pad-bestaat-niet")

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("Pad kon niet geresolved worden: pad-bestaat-niet")
    }

    @Test
    fun `when the file argument is missing then the use case is not called`() {
        val result = runCli("registreer", "--bulk")

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).isNotEmpty()
    }

    @Test
    fun `when the bulk argument is missing but the path argument points to a directory then feedback is provided and the use case is not called`() {
        val padNaarMap = schrijfMap(setOf("bestand.eml"))

        val result = runCli("registreer", padNaarMap)

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("Om meerdere bestanden te imperteren gebruik je het --bulk argument en verwijs je naar een folder.")
    }

    @Test
    fun `when the bulk argument is provided but the path points to a file`() {
        val padNaarFile = tempDir.resolve("mail.eml")
        padNaarFile.writeBytes(RawEmailTestBuilder().build().content)

        val result = runCli("registreer", "--bulk", padNaarFile.toString())

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("Om meerdere bestanden te imperteren verwijs je naar een geldige folder.")
    }

    private fun setupDirectory(bestanden: Map<String, MockRegistreerEmail.TestAction>): String {
        bestanden.values.forEach { context.registreerEmail.setNextAction(it) }
        return schrijfMap(bestanden.keys)
    }

    private fun schrijfMap(bestandsnamen: Set<String>): String {
        val map = tempDir.resolve("import")

        bestandsnamen.forEach { naam ->
            val bestand = map.resolve(naam)
            bestand.parent.createDirectories()
            bestand.writeBytes(RawEmailTestBuilder().build().content)
        }

        return map.toString()
    }
}