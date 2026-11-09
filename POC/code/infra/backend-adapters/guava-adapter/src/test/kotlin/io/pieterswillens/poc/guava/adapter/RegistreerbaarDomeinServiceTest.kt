package io.pieterswillens.poc.guava.adapter

import io.pieterswillens.poc.domain.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.RegistreerbaarDomeinService
import io.pieterswillens.poc.testdoubles.MockRegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import java.util.stream.Stream

interface RegistreerbaarDomeinServiceTest {
    fun service(): RegistreerbaarDomeinService

    @ParameterizedTest
    @ArgumentsSource(ValidExamples::class)
    fun `when domainname has parsable registreerbaar domein then the registreerbaar domein Ok is returned containing registreerbaar domein`(input: EmailAdres, expectedRegistreerbaarDomein: String) {
        val response = service().bepaalRegistreerbaarDomein(input.domeinnaam)

        assertThat(response).isInstanceOfSatisfying(BepaalRegistreerbaarDomeinResponse.Ok::class.java) {
            assertThat(it.registreerbaarDomein).isEqualTo(expectedRegistreerbaarDomein)
        }
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidExamples::class)
    fun `when domainname has no parsable registreerbaar domein then Ongelidg is returned`(input: EmailAdres) {
        val response = service().bepaalRegistreerbaarDomein(input.domeinnaam)

        assertThat(response).isEqualTo(BepaalRegistreerbaarDomeinResponse.Ongeldig)
    }

    private class ValidExamples : ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(EmailAdres.from("test@email.be"), "email.be"),
                Arguments.of(EmailAdres.from("test@EMAIL.be"), "email.be"),
                Arguments.of(EmailAdres.from("test@subdomein.email.be"), "email.be"),
                Arguments.of(EmailAdres.from("test@Subdomein.Email.BE"), "email.be"),
                Arguments.of(EmailAdres.from("test@meerdere.subdomeinen.email.be"), "email.be")
            )
        }
    }

    private class InvalidExamples : ArgumentsProvider {
        override fun provideArguments(
            parameters: ParameterDeclarations,
            context: ExtensionContext
        ): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(EmailAdres.from("test@email")),
                Arguments.of(EmailAdres.from("test@email!be"))
            )
        }
    }
}

@DisplayName("Given a guava service to bepaalRegistreerbaarDomein")
class GuavaRegistreerbaarDomeinServiceTest: RegistreerbaarDomeinServiceTest {
    override fun service() = GuavaRegistreerbaarDomeinService()
}

@DisplayName("Given a mock service to bepaalRegistreerbaarDomein")
class MockRegistreerbaarDomeinServiceTest: RegistreerbaarDomeinServiceTest {
    override fun service() = MockRegistreerbaarDomeinService()
}