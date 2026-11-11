package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.acceptance.tests.config.AcceptanceTest
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.testdoubles.RawEmailTestBuilder
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.vocabulary.EmailId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Given an application api to registreer an email")
class EmailTest: AcceptanceTest {

    private lateinit var savedEmailId: EmailId

    @Test
    fun `when testing the flow of an email`() {
        whenRegisteringAnEmail()
        thenTheEmailShouldBeSaved()
    }

    private fun whenRegisteringAnEmail() {
        val input = RawEmailTestBuilder().build()

        val presenter = RegistreerEmailTestPresenter()
        context.registreerEmail.registreerEmail(
            input = RegistreerEmailInput(input.content),
            presenter = presenter
        )

        savedEmailId = presenter.presentedEmailId
    }

    // TODO: rewrite this to use application api get once it has been implemented
    private fun thenTheEmailShouldBeSaved() {
        val isSaved = context.emailRepository.storedItems.any { email ->
            email.emailId == savedEmailId
        }

        assertThat(isSaved).isTrue()
    }

    class RegistreerEmailTestPresenter: RegistreerEmailPresenter {
        lateinit var presentedEmailId: EmailId

        override fun emailRegistered(emailId: EmailId) {
            presentedEmailId = emailId
        }

        override fun emailRegistrationFailed() = TestUtils.fail()
        override fun emailAlreadyRegistered(emailId: EmailId) = TestUtils.fail()
    }
}
