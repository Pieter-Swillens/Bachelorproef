package io.pieterswillens.poc.acceptance.tests.config

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.testdoubles.MockEmailRepository
import io.pieterswillens.poc.testdoubles.StubbedEmailParser
import io.pieterswillens.poc.use.cases.RegistreerEmailUseCase

class BlackboxApplicationContext {
    /* --- REPOSITORIES --- */
    val emailRepository: MockEmailRepository = MockEmailRepository()

    /* --- SERVICES --- */
    private val emailParser = StubbedEmailParser()

    /* --- USE CASES --- */
    val registreerEmail: RegistreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository
    )

    fun reset() {
        emailRepository.clear()
    }

    companion object {
        val context = BlackboxApplicationContext()
    }
}
