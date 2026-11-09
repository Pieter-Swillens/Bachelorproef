package io.pieterswillens.poc.vocabulary

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Given a string that should represent a mail address")
class EmailAdresTest {

    @Nested
    internal inner class `when the input is valid` {

        private val adres = EmailAdres.from("test@email.be")

        @Test
        fun `then the local part is everything before the at sign`() {
            assertThat(adres.lokaalDeel.value).isEqualTo("test")
        }

        @Test
        fun `then the domain name is everything after the at sign`() {
            assertThat(adres.domeinnaam.value).isEqualTo("email.be")
        }

    }

    @Nested
    internal inner class `when the input has no at sign` {

        @ParameterizedTest(name = "[{0}]")
        @ValueSource(strings = ["", "geen-adres", "voorbeeld.email.be"])
        fun `then an exception is thrown`(input: String) {
            assertThatThrownBy { EmailAdres.from(input) }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    internal inner class `when the lokale deel is missing or blank` {

        @ParameterizedTest(name = "[{0}]")
        @ValueSource(strings = ["@email.be", "   @bank.be"])
        fun `then an exception is thrown`(input: String) {
            assertThatThrownBy { EmailAdres.from(input) }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    internal inner class `when the domeinnaam is missing or blank` {

        @ParameterizedTest(name = "[{0}]")
        @ValueSource(strings = ["info@", "info@ ", "info@   "])
        fun `then an exception is thrown`(input: String) {
            assertThatThrownBy { EmailAdres.from(input) }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}