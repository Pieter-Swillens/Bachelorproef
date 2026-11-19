package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.domain.email.EmailRepository

interface ApplicationContext {
    val registreerEmail: RegistreerEmail
    val emailRepository: EmailRepository
}