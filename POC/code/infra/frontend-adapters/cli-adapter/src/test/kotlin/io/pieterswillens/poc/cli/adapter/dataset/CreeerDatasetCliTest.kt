package io.pieterswillens.poc.cli.adapter.dataset

import io.pieterswillens.poc.cli.adapter.config.CliTest
import io.pieterswillens.poc.cli.adapter.mocks.MockCreeerDataset.TestAction.*
import io.pieterswillens.poc.vocabulary.dataset.Seed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Given a CLI interface allowing a user to create a dataset")
class CreeerDatasetCliTest : CliTest {

    private val command = "mkdataset"

    @Test
    fun `when succesfully creating a dataset then feedback is provided`() {
        context.creeerDataset.setNextAction(SUCCESSFULLY_CREATED)

        val result = runCli(command)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("Dataset successvol gecreëerd.")
        assertThat(result.stderr).isEmpty()
        assertThat(context.creeerDataset.usedSeed).isEqualTo(Seed.DEFAULT)
    }

    @ParameterizedTest
    @ValueSource(strings = ["-s", "--seed"])
    fun `when succesfully creating a dataset with the seed provided then feedback is provided`(argument: String) {
        context.creeerDataset.setNextAction(SUCCESSFULLY_CREATED)

        val result = runCli(command, argument, "1234")

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("Dataset successvol gecreëerd.")
        assertThat(result.stderr).isEmpty()
        assertThat(context.creeerDataset.usedSeed).isEqualTo(Seed(1234))
    }

    @Test
    fun `when the sampleset is too small then feedback is provided`() {
        context.creeerDataset.setNextAction(FAILED_SAMPLESET_TOO_SMALL)

        val result = runCli(command)

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(result.stdout).isEmpty()
        assertThat(result.stderr).isEqualTo("Dataset kon niet worden gecreëerd: er moeten minstens 10 geregistreerde e-mails zijn.")
    }

    @Test
    fun `when the sampleset is lopsided containing not enough examples of each emailAard then feedback is provided`() {
        context.creeerDataset.setNextAction(FAILED_NOT_ENOUGH_EXAMPLES_OF_SPECIFIC_EMAIL_AARD)

        val result = runCli(command)

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(result.stdout).isEmpty()
        assertThat(result.stderr).isEqualTo("Dataset kon niet worden gecreëerd: moeten minstens 2 e-mails van elke klasse (phishing of legitiem) geregistreerd zijn.")
    }

    @ParameterizedTest
    @ValueSource(strings = ["invalid", "  1234  ", "1.5", "1,5", "\t", ""])
    fun `when the provided seed option is invalid then the use case is not executed`(invalidSeed: String) {
        context.creeerDataset.setNextAction(SUCCESSFULLY_CREATED)

        val result = runCli(command, "-s", invalidSeed)

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(result.stdout).isEmpty()
        assertThat(result.stderr).containsIgnoringWhitespaces("$invalidSeed is een ongeldige seed. De seed moet een geheel getal zijn.")
        assertThat(context.creeerDataset.usedSeed).isNull()
    }

    @Test
    fun `when the command is unknown then the use case is not called`() {
        val result = runCli("bestaat-niet")

        assertThat(result.exitCode).isEqualTo(64)
        assertThat(result.stderr).contains("bestaat-niet")
        assertThat(context.creeerDataset.usedSeed).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["-s 1234", "--seed 1234", "--seed=1234"])
    fun `when running the command in an interactive shell then the responses do not deviate from the normal cli usage`(arguments: String) {
        context.creeerDataset.setNextAction(SUCCESSFULLY_CREATED)

        val result = runInteractiveCli("$command -s abc", "$command $arguments", "exit")

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stderr).contains("is een ongeldige seed")
        assertThat(result.stdout).contains("gecreëerd")
        assertThat(context.creeerDataset.usedSeed).isEqualTo(Seed(1234))
    }

    @ParameterizedTest
    @ValueSource(strings = ["-h", "--help"])
    fun `when the user requests help then documentation is provided`(argument: String) {
        val expectedOutput = javaClass.getResource("/mkdataset-help-output.txt")!!.readText()

        val result = runCli(command, argument)

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo(expectedOutput)
    }

}