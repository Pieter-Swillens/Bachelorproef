package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailSnapshot
import io.pieterswillens.poc.testdoubles.email.MockRegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.EmailMessageId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream

@DisplayName("Given an extractor to check whether From and Return-Path headers refer to the same organisation")
class FromWijktAfVanReturnPathTest {

    private val registreerbaarDomeinService = MockRegistreerbaarDomeinService()
    private val extractor = FromWijktAfVanReturnPath(registreerbaarDomeinService)

    @Nested
    internal inner class `when registreerbaar domein in From header is the same as the one in the Return-Path header`{

        private lateinit var result: ExtractieResult

        fun setUp(email: Email) {
            result = extractor.evaluate(email)
        }

        @ParameterizedTest
        @ArgumentsSource(NietAfwijkendExamplesTestParams::class)
        fun `then extractor should report NietAfwijkend`(email: Email) {
            setUp(email)

            assertThat(result).isEqualTo(ExtractieResult.NietAfwijkend)
        }
    }

    @Nested
    internal inner class `when registreerbaar domein in From header is not the same as the one in the ReturnPath header`{

        private lateinit var result: ExtractieResult
        private val email = Email.fromSnapshot(
            EmailSnapshot(
                emailId = EmailId.createNew(),
                fromEmail = EmailAdres.from("test@valid.com"),
                replyToEmail = EmailAdres.from("test@phishing.com"),
                returnPathEmail = EmailAdres.from("test@phishing.com"),
                emailMessageId = EmailMessageId.random()
            )
        )

        @BeforeEach
        fun setUp() {
            result = extractor.evaluate(email)
        }

        @Test
        fun `then extractor should report Afwijkend and provide evidence for the inconsistency`() {
            assertThat(result).isInstanceOfSatisfying(ExtractieResult.Afwijkend::class.java) {
                assertThat(it.bewijs).isEqualTo("De afzender van de email (${email.fromEmail}) wordt niet op de hoogte gebracht van een bericht dat niet kon worden afgeleverd. Deze melding wordt verstuurd naar ${email.returnPath}")
            }
        }
    }

    @Nested
    internal inner class `when necessary data to evaluate condition is missing or invalid`{

        private lateinit var result: ExtractieResult

        fun setUp(email: Email) {
            result = extractor.evaluate(email)
        }

        @ParameterizedTest
        @ArgumentsSource(NietBepaalbareExamplesTestParams::class)
        fun `then extractor should report NietBepaalbaar`(email: Email) {
            setUp(email)

            assertThat(result).isEqualTo(ExtractieResult.NietBepaalbaar)
        }
    }

    class NietAfwijkendExamplesTestParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@email.be"),
                            replyToEmail = EmailAdres.from("test@email.be"),
                            returnPathEmail = EmailAdres.from("test@email.be"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@email.be"),
                            replyToEmail = EmailAdres.from("nogeentest@email.be"),
                            returnPathEmail = EmailAdres.from("nogeentest@email.be"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@email.be"),
                            replyToEmail = EmailAdres.from("test@subdomein.email.be"),
                            returnPathEmail = EmailAdres.from("test@subdomein.email.be"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@EMAIL.be"),
                            replyToEmail = EmailAdres.from("test@email.BE"),
                            returnPathEmail = EmailAdres.from("test@email.BE"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
            )
        }
    }

    class NietBepaalbareExamplesTestParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@localhost"),
                            replyToEmail = EmailAdres.noOps,
                            returnPathEmail = EmailAdres.from("test@email.be"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@email.be"),
                            replyToEmail = EmailAdres.noOps,
                            returnPathEmail = EmailAdres.from("test@localhost"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@email!be"),
                            replyToEmail = EmailAdres.noOps,
                            returnPathEmail = EmailAdres.from("test@email.be"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
                Arguments.of(
                    Email.fromSnapshot(
                        EmailSnapshot(
                            emailId = EmailId.createNew(),
                            fromEmail = EmailAdres.from("test@email.be"),
                            replyToEmail = EmailAdres.noOps,
                            returnPathEmail = EmailAdres.from("test@email!be"),
                            emailMessageId = EmailMessageId.random()
                        )
                    )
                ),
            )
        }
    }

}