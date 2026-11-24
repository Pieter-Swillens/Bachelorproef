package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailSnapshot
import io.pieterswillens.poc.testdoubles.email.MockRegistreerbaarDomeinService
import io.pieterswillens.poc.testdoubles.email.testbuilders.EmailSnapshotTestBuilder
import io.pieterswillens.poc.vocabulary.EmailAdres
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream

@DisplayName("Given an extractor to determine how many different registreerbare domeinen are present")
class AantalVerschillendeDomeinenTest {

    private val registreerbaarDomeinService = MockRegistreerbaarDomeinService()
    private val extractor = AantalVerschillendeDomeinen(registreerbaarDomeinService)

    @Nested
    internal inner class `when extracting the registreerbare domeinen` {

        private lateinit var result: ExtractieResult

        fun setUp(email: Email) {
            result = extractor.evaluate(email)
        }

        @ParameterizedTest
        @ArgumentsSource(TestParams::class)
        fun `then extractor should report Gemeten and contain the amount of different registreerbare domeinen`(snapshot: EmailSnapshot, expectedWaarde: Int) {
            setUp(Email.fromSnapshot(snapshot))

            assertThat(result).isInstanceOfSatisfying(ExtractieResult.Gemeten::class.java) {
                assertThat(it.waarde).isEqualTo(expectedWaarde)
            }
        }
    }

    class TestParams : ArgumentsProvider {
        private val firstEmailAdres = EmailAdres.from("pieter.swillens@student.hogent.be")
        private val secondEmailAdres = EmailAdres.from("pieter.swillens@gmail.com")
        private val thirdEmailAdres = EmailAdres.from("pieter.swillens@athumi.eu")

        private val emailWithADomein = EmailAdres.from("een.test@student.hogent.be")
        private val anotherEmailWithTheSameDomein = EmailAdres.from("test-twee@student.hogent.be")

        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = firstEmailAdres, replyToEmail = firstEmailAdres, returnPathEmail = firstEmailAdres).build(), 1),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = firstEmailAdres, replyToEmail = secondEmailAdres, returnPathEmail = firstEmailAdres).build(), 2),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = firstEmailAdres, replyToEmail = firstEmailAdres, returnPathEmail = secondEmailAdres).build(), 2),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = secondEmailAdres, replyToEmail = firstEmailAdres, returnPathEmail = firstEmailAdres).build(), 2),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = secondEmailAdres, replyToEmail = thirdEmailAdres, returnPathEmail = firstEmailAdres).build(), 3),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = secondEmailAdres, replyToEmail = firstEmailAdres, returnPathEmail = secondEmailAdres).build(), 2),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = firstEmailAdres, replyToEmail = secondEmailAdres, returnPathEmail = thirdEmailAdres).build(), 3),
                Arguments.of(EmailSnapshotTestBuilder().with(fromEmail = emailWithADomein, replyToEmail = emailWithADomein, returnPathEmail = anotherEmailWithTheSameDomein).build(), 1)
            )
        }
    }
}
