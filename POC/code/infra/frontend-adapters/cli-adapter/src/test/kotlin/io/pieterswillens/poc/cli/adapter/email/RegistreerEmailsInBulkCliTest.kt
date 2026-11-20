package io.pieterswillens.poc.cli.adapter.email

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

@DisplayName("Given a CLI interface allowing a user to register multiple new emails")
class RegistreerEmailsInBulkCliTest: CliTest {

    @TempDir
    lateinit var tempDir: Path

    @ParameterizedTest
    @ValueSource(strings = ["--bulk", "-b"])
    fun `when successfully registering at least one email from multiple emails then OK feedback is provided`(bulkArgument: String) {
        val padNaarMap = setupDirectory(
            mapOf(
                "legit/bestand1.eml" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand2.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "legit/bestand3.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED,
                "submap/legit/bestand4.eml" to MockRegistreerEmail.TestAction.REGISTERED,
                "submap/legit/bestand5.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "submap/legit/bestand6.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED
            )
        )

        val result = runCli("registreer", bulkArgument, padNaarMap)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).contains(
            "E-mail geregistreerd - emailId: ${context.registreerEmail.emailIds[0].value}",
            "E-mail geregistreerd - emailId: ${context.registreerEmail.emailIds[3].value}",
            """
                Resultaat van de bulk import:
                    Aantal nieuwe registraties:     2
                    Aantal reeds geregistreerd:     2
                    Aantal mislukt:                 2
            """.trimIndent()
        )
        assertThat(result.stderr).isEqualTo("""
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[1].value}
            E-mail kon niet worden verwerkt: legit/bestand3.eml
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[4].value}
            E-mail kon niet worden verwerkt: submap/legit/bestand6.eml
        """.trimIndent())
    }

    @Test
    fun `when all registering multiple emails and all of them fail then MISLUKT feedback is provided`() {
        val padNaarMap = setupDirectory(
            mapOf(
                "legit/bestand2.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "legit/bestand3.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED,
                "legit/submap/bestand5.eml" to MockRegistreerEmail.TestAction.ALREADY_REGISTERED,
                "legit/submap/bestand6.eml" to MockRegistreerEmail.TestAction.REGISTRATION_FAILED
            )
        )

        val result = runCli("registreer", "--bulk", padNaarMap)

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(result.stdout).contains(
            "Aantal reeds geregistreerd:     2",
            "Aantal mislukt:                 2"
        )
        assertThat(result.stderr).isEqualTo("""
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[0].value}
            E-mail kon niet worden verwerkt: legit/bestand3.eml
            E-mail is al geregistreerd - emailId: ${context.registreerEmail.emailIds[2].value}
            E-mail kon niet worden verwerkt: legit/submap/bestand6.eml
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

    @Test
    fun `when the the directory contains unsupported file extensions then unsupported files are ignored and OK feedback is provided`() {
        val padNaarMap = setupDirectory(
            mapOf(
                "legit/bestand1.pdf" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand2.docx" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand3.xlsx" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand3.png" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand5.mp4" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand6.zip" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand7.eml" to MockRegistreerEmail.TestAction.REGISTERED,
            )
        )

        val result = runCli("registreer", "--bulk", padNaarMap)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(context.registreerEmail.emailIds).hasSize(1)
    }

    @Test
    fun `when the the directory consists of only unsupported file extensions then MISLUKT feedback is provided`() {
        val padNaarMap = setupDirectory(
            mapOf(
                "legit/bestand1.pdf" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand2.docx" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand3.xlsx" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand3.png" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand5.mp4" to MockRegistreerEmail.TestAction.REGISTERED,
                "legit/bestand6.zip" to MockRegistreerEmail.TestAction.REGISTERED,
            )
        )

        val result = runCli("registreer", "--bulk", padNaarMap)

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(context.registreerEmail.emailIds).hasSize(0)
    }

    @Test
    fun `when the aard of the email cannot be determined`() {
        val padNaarMap = setupDirectory(
            mapOf(
                "submap/bestand1.eml" to MockRegistreerEmail.TestAction.REGISTERED,
                "submap/bestand2.eml" to MockRegistreerEmail.TestAction.REGISTERED,
            )
        )

        val result = runCli("registreer", "--bulk", padNaarMap)

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(context.registreerEmail.receivedInput).isNull()
        assertThat(result.stderr).contains("Label kon niet worden toegekend omdat het pad 'legit' of 'phishing' moet bevatten")
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