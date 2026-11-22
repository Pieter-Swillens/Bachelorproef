package io.pieterswillens.poc.acceptance.tests.traits

import io.pieterswillens.poc.acceptance.tests.ApplicationContext
import io.pieterswillens.poc.acceptance.tests.presenters.RegistreerEmailTestPresenter
import io.pieterswillens.poc.api.RegistreerEmailInput
import io.pieterswillens.poc.testdoubles.email.testbuilders.RawEmailTestBuilder
import io.pieterswillens.poc.vocabulary.EmailAard

interface DatasetTraits {
    val context: ApplicationContext

    fun givenAValidSampleSet() {
        for (i in 1..100) {
            val email = RawEmailTestBuilder().with(
                bestandsnaam = "email-$i.eml",
                emailAard = EmailAard.entries.random()
            ).build()

            registreerEmail(email.bestandsnaam, email.emailAard, email.content)
        }
    }

    private fun registreerEmail(bestandsnaam: String, emailAard: EmailAard, content: ByteArray) {
        context.registreerEmail.registreerEmail(
            input = RegistreerEmailInput(
                bestandsnaam = bestandsnaam,
                emailAard = emailAard,
                content = content
            ),
            presenter = RegistreerEmailTestPresenter()
        )
    }
}