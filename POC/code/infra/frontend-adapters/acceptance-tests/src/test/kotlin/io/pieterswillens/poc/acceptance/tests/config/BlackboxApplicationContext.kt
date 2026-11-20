package io.pieterswillens.poc.acceptance.tests.config

import io.pieterswillens.poc.acceptance.tests.ApplicationContext
import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.email.MockDetectiekenmerken
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.StubbedEmailParser
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.email.RegistreerEmailUseCase
import io.pieterswillens.poc.use.cases.event.handlers.EmailEventHandler

class BlackboxApplicationContext: ApplicationContext {

    private val eventPublisher = MockEventPublisher()

    /* --- REPOSITORIES --- */
    override val emailRepository: MockEmailRepository = MockEmailRepository()

    /* --- SERVICES --- */
    private val emailParser = StubbedEmailParser()

    /* --- REGISTRIES --- */
    private val detectiekenmerken = MockDetectiekenmerken()

    /* --- USE CASES --- */
    override val registreerEmail: RegistreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )
    val berekenDetectiekenmerken = BerekenDetectiekenmerkenUseCase(
        detectiekenmerken = detectiekenmerken,
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
