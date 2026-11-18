package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.acceptance.tests.config.AcceptanceTest
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.testdoubles.email.testbuilders.RawEmailTestBuilder
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
        thenTheDetectiekenmerkenShouldBeBerekend()
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
        val savedEmail = context.emailRepository.get(savedEmailId)

        assertThat(savedEmail).isNotNull()
    }

    private fun thenTheDetectiekenmerkenShouldBeBerekend() {
        val savedEmail = context.emailRepository.get(savedEmailId)

        assertThat(savedEmail!!.status).isInstanceOf(EmailStatus.DetectiekenmerkenBerekend::class.java)
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
