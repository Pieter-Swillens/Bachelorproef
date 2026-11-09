package io.pieterswillens.poc.domain.detectiekenmerken

import io.pieterswillens.poc.domain.Email
import io.pieterswillens.poc.testdoubles.MockRegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres
import io.pieterswillens.poc.vocabulary.EmailId
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

@DisplayName("Given an extractor to check whether From and Reply-To headers refer to the same organisation")
class FromWijktAfVanReplyToTest {

    private val registreerbaarDomeinService = MockRegistreerbaarDomeinService()
    private val extractor = FromWijktAfVanReplyTo(registreerbaarDomeinService)

    @Nested
    internal inner class `when registreerbaar domein in From header is the same as the one in the ReplyTo header`{

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
    internal inner class `when registreerbaar domein in From header is not the same as the one in the ReplyTo header`{

        private lateinit var result: ExtractieResult
        private val email = Email(
            emailId = EmailId.createNew(),
            fromEmail = EmailAdres.from("test@valid.com"),
            replyToEmail = EmailAdres.from("test@phishing.com")
        )

        @BeforeEach
        fun setUp() {
            result = extractor.evaluate(email)
        }

        @Test
        fun `then extractor should report Afwijkend and provide evidence for the inconsistency`() {
            assertThat(result).isInstanceOfSatisfying(ExtractieResult.Afwijkend::class.java) {
                println(it.bewijs)
                assertThat(it.bewijs).isEqualTo("De afzender van de email (${email.fromEmail}) is niet de ontvanger van je antwoord. Je antwoord wordt verstuurd naar: ${email.replyToEmail}")
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
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email.be"), replyToEmail = EmailAdres.from("test@email.be"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email.be"), replyToEmail = EmailAdres.from("nogeentest@email.be"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email.be"), replyToEmail = EmailAdres.from("test@subdomein.email.be"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@EMAIL.be"), replyToEmail = EmailAdres.from("test@email.BE")))
            )
        }
    }

    class NietBepaalbareExamplesTestParams: ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = null, replyToEmail = EmailAdres.from("test@email.be"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email.be"), replyToEmail = null)),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@localhost"), replyToEmail = EmailAdres.from("test@email.be"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email.be"), replyToEmail = EmailAdres.from("test@localhost"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email!be"), replyToEmail = EmailAdres.from("test@email.be"))),
                Arguments.of(Email(emailId = EmailId.createNew(), fromEmail = EmailAdres.from("test@email.be"), replyToEmail = EmailAdres.from("test@email!be")))
            )
        }
    }
}
