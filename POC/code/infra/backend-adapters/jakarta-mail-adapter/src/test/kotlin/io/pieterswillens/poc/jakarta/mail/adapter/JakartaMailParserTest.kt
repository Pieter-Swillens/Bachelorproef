package io.pieterswillens.poc.jakarta.mail.adapter

import io.pieterswillens.poc.domain.EmailParsingResult
import io.pieterswillens.poc.domain.ParsedEmail
import io.pieterswillens.poc.jakarta.mail.adapter.config.JakartaMailParserConfig
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailMessageId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream

@DisplayName("Given a Jakarta mail parser")
class JakartaMailParserTest {

    val parser = JakartaMailParser(
        session = JakartaMailParserConfig.session()
    )

    @ParameterizedTest
    @ArgumentsSource(ValidEmailTestParams::class)
    fun `when valid mail provided then the mail is succesfully parsed`(filePath: String, expectedEmail: ParsedEmail) {
        val resource = javaClass.getResource(filePath)!!.readBytes()

        val result = parser.parse(resource)

        assertThat(result).isInstanceOfSatisfying(EmailParsingResult.Ok::class.java) {
            assertThat(it.parsedEmail).isEqualTo(expectedEmail)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidEmailTestParams::class)
    fun `when an invalid mail provided then Mislukt is returned`(filePath: String) {
        val resource = javaClass.getResource(filePath)!!.readBytes()

        val result = parser.parse(resource)

        assertThat(result).isEqualTo(EmailParsingResult.Mislukt)
    }

    class ValidEmailTestParams : ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    "/mails/valid/eml/eml-same-from-and-reply-to-mail.eml",
                    ParsedEmail(
                        replyTo = EmailAdres.from("reese.white@dfggameroud222.onmicrosoft.com"),
                        from = EmailAdres.from("reese.white@dfggameroud222.onmicrosoft.com"),
                        messageId = EmailMessageId.from("<CAMuz91Kr6tyUvpP22m95jg4OVCvOkLwCUrZ0VdNQFxPPNp--Ug@mail.gmail.com>")
                    )
                ),
                Arguments.of(
                    "/mails/valid/eml/eml-deviating-from-and-to-mail.eml",
                    ParsedEmail(
                        replyTo = EmailAdres.from("reply@2ndal7.com"),
                        from = EmailAdres.from("bowen.mo@up.ac.pa"),
                        messageId = EmailMessageId.from("<DM6PR08MB5321007CA5B0E0F01F8755BBB1202@DM6PR08MB5321.namprd08.prod.outlook.com>")
                    )
                ),
                Arguments.of(
                    "/mails/valid/txt/txt-same-from-and-reply-to-mail.txt",
                    ParsedEmail(
                        replyTo = EmailAdres.from("kre@munnari.OZ.AU"),
                        from = EmailAdres.from("kre@munnari.OZ.AU"),
                        messageId = EmailMessageId.from("<13258.1030015585@munnari.OZ.AU>")
                    )
                ),
                Arguments.of(
                    "/mails/valid/txt/txt-deviating-from-and-to-mail.txt",
                    ParsedEmail(
                        replyTo = EmailAdres.from("zzzzteana@yahoogroups.com"),
                        from = EmailAdres.from("Stewart.Smith@ee.ed.ac.uk"),
                        messageId = EmailMessageId.from("<3D6C82CB.5040109@ee.ed.ac.uk>")
                    )
                )
            )
        }
    }

    class InvalidEmailTestParams : ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of("/mails/invalid/eml/eml-empty.eml"),
                Arguments.of("/mails/invalid/eml/eml-invalid-email-adress.eml"),
                Arguments.of("/mails/invalid/eml/eml-invalid-headers.eml"),
                Arguments.of("/mails/invalid/eml/eml-random-text.eml"),
                Arguments.of("/mails/invalid/txt/txt-empty.txt"),
                Arguments.of("/mails/invalid/txt/txt-invalid-email-adress.txt"),
                Arguments.of("/mails/invalid/txt/txt-invalid-headers.txt"),
                Arguments.of("/mails/invalid/txt/txt-random-text.txt")
            )
        }
    }
}

