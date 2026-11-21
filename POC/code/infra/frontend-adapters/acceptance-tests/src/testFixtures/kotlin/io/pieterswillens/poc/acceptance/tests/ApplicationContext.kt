package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.domain.dataset.SampleRepository
import io.pieterswillens.poc.domain.email.EmailRepository

interface ApplicationContext {
    val emailRepository: EmailRepository
    val sampleRepository: SampleRepository

    val registreerEmail: RegistreerEmail
}