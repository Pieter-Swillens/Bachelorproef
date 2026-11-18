package io.pieterswillens.poc.acceptance.tests.config

import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.MockRegistreerbaarDomeinService
import io.pieterswillens.poc.testdoubles.email.StubbedEmailParser
import io.pieterswillens.poc.use.cases.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.RegistreerEmailUseCase
import io.pieterswillens.poc.use.cases.event.handlers.EmailEventHandler

class BlackboxApplicationContext {

    private val eventPublisher = MockEventPublisher()

    /* --- REPOSITORIES --- */
    val emailRepository: MockEmailRepository = MockEmailRepository()

    /* --- SERVICES --- */
    private val emailParser = StubbedEmailParser()
    private val registreerbaarDomeinService = MockRegistreerbaarDomeinService()

    /* --- USE CASES --- */
    val registreerEmail: RegistreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )
    val berekenDetectiekenmerken = BerekenDetectiekenmerkenUseCase(
        registreerbaarDomeinService = registreerbaarDomeinService,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )

    init {
        eventPublisher.subscribe(EmailEventHandler(berekenDetectiekenmerken))
    }

    fun reset() {
        emailRepository.clear()
    }

    companion object {
        val context = BlackboxApplicationContext()
    }
}
