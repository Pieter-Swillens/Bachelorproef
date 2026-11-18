package io.pieterswillens.poc.main.dependencies

import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.inmemory.eventpublisher.InMemoryEventPublisher
import io.pieterswillens.poc.jakarta.mail.adapter.JakartaMailParser
import io.pieterswillens.poc.jakarta.mail.adapter.config.JakartaMailParserConfig
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.MockRegistreerbaarDomeinService
import io.pieterswillens.poc.use.cases.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.RegistreerEmailUseCase
import io.pieterswillens.poc.use.cases.event.handlers.EmailEventHandler

internal object Dependencies {
    private val eventHandlers = mutableListOf<EventHandler>()
    private val eventPublisher = InMemoryEventPublisher(eventHandlers)

    private val emailParser = JakartaMailParser(JakartaMailParserConfig)
    private val emailRepository = MockEmailRepository()

    val registreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )
    val berekenDetectiekenmerkenUseCase = BerekenDetectiekenmerkenUseCase(
        registreerbaarDomeinService = MockRegistreerbaarDomeinService(),
        emailRepository = emailRepository,
        eventPublisher = eventPublisher
    )

    init {
        eventHandlers.add(EmailEventHandler(berekenDetectiekenmerkenUseCase))
    }
}