package io.pieterswillens.poc.main.dependencies

import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkenRegistry
import io.pieterswillens.poc.domain.email.detectiekenmerken.FromWijktAfVanReplyTo
import io.pieterswillens.poc.guava.adapter.GuavaRegistreerbaarDomeinService
import io.pieterswillens.poc.inmemory.eventpublisher.InMemoryEventPublisher
import io.pieterswillens.poc.jakarta.mail.adapter.JakartaMailParser
import io.pieterswillens.poc.jakarta.mail.adapter.config.JakartaMailParserConfig
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.email.RegistreerEmailUseCase
import io.pieterswillens.poc.use.cases.event.handlers.EmailEventHandler

internal object Dependencies {

    /* --- REPOSITORIES & INFRA --- */
    private val eventHandlers = mutableListOf<EventHandler>()
    private val eventPublisher = InMemoryEventPublisher(eventHandlers)

    private val emailParser = JakartaMailParser(JakartaMailParserConfig)
    internal val emailRepository = MockEmailRepository()

    /* --- SERVICES --- */
    private val registreerbaarDomeinService = GuavaRegistreerbaarDomeinService()

    /* --- REGISTRIES --- */
    private val detectiekenmerkenRegistry = DetectiekenmerkenRegistry(
        kenmerken = listOf(
            FromWijktAfVanReplyTo(registreerbaarDomeinService)
        )
    )

    /* --- USE CASES --- */
    val registreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )
    val berekenDetectiekenmerkenUseCase = BerekenDetectiekenmerkenUseCase(
        detectiekenmerken = detectiekenmerkenRegistry,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )

    init {
        eventHandlers.add(EmailEventHandler(berekenDetectiekenmerkenUseCase))
    }
}