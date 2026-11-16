package io.pieterswillens.poc.acceptance.tests.config

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.StubbedEmailParser
import io.pieterswillens.poc.use.cases.RegistreerEmailUseCase

class BlackboxApplicationContext {

    private val eventPublisher = MockEventPublisher()

    /* --- REPOSITORIES --- */
    val emailRepository: MockEmailRepository = MockEmailRepository()

    /* --- SERVICES --- */
    private val emailParser = StubbedEmailParser()

    /* --- USE CASES --- */
    val registreerEmail: RegistreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )

    fun reset() {
        emailRepository.clear()
    }

    companion object {
        val context = BlackboxApplicationContext()
    }
}
