package io.pieterswillens.poc.cli.adapter.shell

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream

@DisplayName("Given a tokenizer for lines typed in the interactive CLI")
class CommandLineTokenizerTest {

    @ParameterizedTest(name = "[{index}] <{0}> is split into {1}")
    @ArgumentsSource(TestParams::class)
    fun `when a line is tokenized then it is split into arguments`(line: String, expected: List<String>) {
        Assertions.assertThat(CommandLineTokenizer.tokenize(line)).isEqualTo(expected)
    }

    class TestParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of("", emptyList<String>()),
                Arguments.of("   ", emptyList<String>()),
                Arguments.of("registreer", listOf("registreer")),
                Arguments.of("registreer   mail.eml", listOf("registreer", "mail.eml")),
                Arguments.of("registreer\t--bulk\tmap", listOf("registreer", "--bulk", "map")),
                Arguments.of("registreer \"map met spaties/mail.eml\"", listOf("registreer", "map met spaties/mail.eml")),
                Arguments.of("registreer 'map met spaties/mail.eml'", listOf("registreer", "map met spaties/mail.eml")),
                Arguments.of("registreer \"it's here\"", listOf("registreer", "it's here")),
                Arguments.of("a\"b c\"d", listOf("ab cd")),
                Arguments.of("registreer \"\"", listOf("registreer", "")),
                Arguments.of("registreer \"niet afgesloten", listOf("registreer", "niet afgesloten")),
                Arguments.of("registreer C:\\mails\\a.eml", listOf("registreer", "C:\\mails\\a.eml")),
            )
        }
    }
}