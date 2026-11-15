package io.pieterswillens.poc.cli.adapter.mocks

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.vocabulary.EmailId

class MockRegistreerEmail : RegistreerEmail, MockApplicationApi<MockRegistreerEmail.TestAction> {

    private val nextTestActions = ArrayDeque<TestAction>()

    var emailIds: MutableList<EmailId> = mutableListOf()
        private set
    val emailId: EmailId?
        get() = emailIds.lastOrNull()
    var receivedInputs: MutableList<RegistreerEmailInput> = mutableListOf()
        private set
    val receivedInput: RegistreerEmailInput?
        get() = receivedInputs.lastOrNull()

    override fun registreerEmail(input: RegistreerEmailInput, presenter: RegistreerEmailPresenter) {
        receivedInputs.addLast(input)
        val emailId = EmailId.createNew()
        emailIds.addLast(emailId)

        when (val action = nextTestActions.removeFirstOrNull()) {
            TestAction.REGISTERED -> presenter.emailRegistered(emailId)
            TestAction.REGISTRATION_FAILED -> presenter.emailRegistrationFailed()
            TestAction.ALREADY_REGISTERED -> presenter.emailAlreadyRegistered(emailId)
            else -> error("Unknown test action: $action")
        }
    }

    override fun setNextAction(action: TestAction) {
        nextTestActions.addLast(action)
    }

    override fun reset() {
        nextTestActions.clear()
        emailIds.clear()
        receivedInputs.clear()
    }

    enum class TestAction {
        REGISTERED,
        REGISTRATION_FAILED,
        ALREADY_REGISTERED
    }
}