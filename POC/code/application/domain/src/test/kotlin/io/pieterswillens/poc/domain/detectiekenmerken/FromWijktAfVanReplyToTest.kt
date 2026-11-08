package io.pieterswillens.poc.domain.detectiekenmerken

import io.pieterswillens.poc.domain.Email
import io.pieterswillens.poc.vocabulary.EmailId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Given an xyz to check if from and reply to fields are consistent")
class FromWijktAfVanReplyToTest {

    private val extractor = FromWijktAfVanReplyTo

    @Nested
    internal inner class `when From header is the same as the ReplyTo header`{

        private lateinit var result: ExtractieResult

        @BeforeEach
        fun setUp() {
            result = extractor.evaluate(eenConsistenteEmail)
        }

        @Test
        fun `then extractor should report NietAfwijkend`() {
            assertThat(result).isEqualTo(ExtractieResult.NietAfwijkend)
        }
    }

    @Nested
    internal inner class `when From header is not the same as the ReplyTo header`{

        private lateinit var result: ExtractieResult

        @BeforeEach
        fun setUp() {
            result = extractor.evaluate(eenIncosistenteEmail)
        }

        @Test
        fun `then extractor should report Afwijkend and provide evidence for the inconsistency`() {
            assertThat(result).isInstanceOfSatisfying(ExtractieResult.Afwijkend::class.java) {
                assertThat(it.bewijs).isEqualTo("De afzender van de email (${eenIncosistenteEmail.fromHeader}) is niet de ontvanger van je antwoord. Je antwoord wordt verstuurd naar: ${eenIncosistenteEmail.replyToHeader}.")
            }
        }
    }

    @Nested
    internal inner class `when From header is missing`{

        private lateinit var result: ExtractieResult

        @BeforeEach
        fun setUp() {
            result = extractor.evaluate(eenConsistenteEmail.copy(fromHeader = null))
        }

        @Test
        fun `then extractor should report NietBepaalbaar`() {
            assertThat(result).isEqualTo(ExtractieResult.NietBepaalbaar)
        }
    }

    @Nested
    internal inner class `when ReplyTo header is missing`{

        private lateinit var result: ExtractieResult

        @BeforeEach
        fun setUp() {
            result = extractor.evaluate(eenConsistenteEmail.copy(replyToHeader = null))
        }

        @Test
        fun `then extractor should report NietBepaalbaar`() {
            assertThat(result).isEqualTo(ExtractieResult.NietBepaalbaar)
        }
    }

    val eenConsistenteEmail = Email(
        emailId = EmailId.createNew(),
        fromHeader = "een@email.com",
        replyToHeader = "een@email.com",
    )

    val eenIncosistenteEmail = eenConsistenteEmail.copy(replyToHeader = "inconsistent@email.com")
}