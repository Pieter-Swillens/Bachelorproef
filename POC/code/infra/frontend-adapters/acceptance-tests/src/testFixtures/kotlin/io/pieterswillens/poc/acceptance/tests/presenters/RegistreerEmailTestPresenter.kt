package io.pieterswillens.poc.acceptance.tests.presenters

import io.pieterswillens.poc.api.RegistreerEmailPresenter
import io.pieterswillens.poc.testdoubles.TestUtils
import io.pieterswillens.poc.vocabulary.EmailId

class RegistreerEmailTestPresenter: RegistreerEmailPresenter {
        lateinit var presentedEmailId: EmailId

        override fun emailRegistered(emailId: EmailId) {
            presentedEmailId = emailId
        }

        override fun emailRegistrationFailed() = TestUtils.fail()
        override fun emailAlreadyRegistered(emailId: EmailId) = TestUtils.fail()
    }
