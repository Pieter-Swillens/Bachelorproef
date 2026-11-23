package io.pieterswillens.poc.main.dependencies

import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkenRegistry
import io.pieterswillens.poc.domain.email.detectiekenmerken.FromWijktAfVanReplyTo
import io.pieterswillens.poc.domain.email.detectiekenmerken.FromWijktAfVanReturnPath
import io.pieterswillens.poc.guava.adapter.GuavaRegistreerbaarDomeinService
import io.pieterswillens.poc.inmemory.eventpublisher.InMemoryEventPublisher
import io.pieterswillens.poc.jakarta.mail.adapter.JakartaMailParser
import io.pieterswillens.poc.jakarta.mail.adapter.config.JakartaMailParserConfig
import io.pieterswillens.poc.testdoubles.dataset.MockDatasetRepository
import io.pieterswillens.poc.testdoubles.dataset.MockSampleRepository
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.use.cases.dataset.CreeerDatasetUseCase
import io.pieterswillens.poc.use.cases.dataset.SlaSampleOpUseCase
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.email.RegistreerEmailUseCase
import io.pieterswillens.poc.use.cases.event.handlers.DatasetEventHandler
import io.pieterswillens.poc.use.cases.event.handlers.EmailEventHandler

internal object Dependencies {

    /* --- REPOSITORIES & INFRA --- */
    private val eventHandlers = mutableListOf<EventHandler>()
    private val eventPublisher = InMemoryEventPublisher(eventHandlers)

    private val emailParser = JakartaMailParser(JakartaMailParserConfig)
    internal val emailRepository = MockEmailRepository()
    internal val sampleRepository = MockSampleRepository()
    internal val datasetRepository = MockDatasetRepository()

    /* --- SERVICES --- */
    private val registreerbaarDomeinService = GuavaRegistreerbaarDomeinService()
    internal val geefSamplesService = sampleRepository

    /* --- REGISTRIES --- */
    internal val detectiekenmerkenRegistry = DetectiekenmerkenRegistry(
        kenmerken = listOf(
            FromWijktAfVanReplyTo(registreerbaarDomeinService),
            FromWijktAfVanReturnPath(registreerbaarDomeinService)
        )
    )

    /* --- USE CASES --- */
    internal val registreerEmail = RegistreerEmailUseCase(emailParser, emailRepository, emailRepository, eventPublisher)
    private val berekenDetectiekenmerkenUseCase = BerekenDetectiekenmerkenUseCase(detectiekenmerkenRegistry, emailRepository, eventPublisher)
    private val slaSampleOpUseCase = SlaSampleOpUseCase(sampleRepository, eventPublisher)
    internal val creeerDataset = CreeerDatasetUseCase(geefSamplesService, datasetRepository, eventPublisher)

    init {
        eventHandlers.add(EmailEventHandler(berekenDetectiekenmerkenUseCase))
        eventHandlers.add(DatasetEventHandler(slaSampleOpUseCase))
    }
}