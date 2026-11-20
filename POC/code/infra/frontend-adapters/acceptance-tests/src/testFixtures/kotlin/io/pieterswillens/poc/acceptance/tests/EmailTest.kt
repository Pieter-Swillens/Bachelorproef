package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

interface EmailTest {
    val context: ApplicationContext
    val emailInput: ByteArray

    @Test
    fun `when testing the flow of an email`() {
        val savedEmailId = whenRegisteringAnEmail()
        thenTheEmailShouldBeSaved(savedEmailId)
        thenTheDetectiekenmerkenShouldBeBerekend(savedEmailId)
    }

    private fun whenRegisteringAnEmail(): EmailId {
        val presenter = RegistreerEmailTestPresenter()
        context.registreerEmail.registreerEmail(
            input = RegistreerEmailInput(emailInput, EmailAard.LEGITIEM),
            presenter = presenter
        )

        return presenter.presentedEmailId
    }

    // TODO: rewrite this to use application api get once it has been implemented
    private fun thenTheEmailShouldBeSaved(savedEmailId: EmailId) {
        val savedEmail = context.emailRepository.get(savedEmailId)

        assertThat(savedEmail).isNotNull()
    }

    private fun thenTheDetectiekenmerkenShouldBeBerekend(savedEmailId: EmailId) {
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
