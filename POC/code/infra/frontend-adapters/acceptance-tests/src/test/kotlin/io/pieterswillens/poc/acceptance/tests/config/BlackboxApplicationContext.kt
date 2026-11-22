package io.pieterswillens.poc.acceptance.tests.config

import io.pieterswillens.poc.acceptance.tests.ApplicationContext
import io.pieterswillens.poc.api.CreeerDataset
import io.pieterswillens.poc.api.RegistreerEmail
import io.pieterswillens.poc.testdoubles.MockEventPublisher
import io.pieterswillens.poc.testdoubles.dataset.MockDatasetRepository
import io.pieterswillens.poc.testdoubles.dataset.MockSampleRepository
import io.pieterswillens.poc.testdoubles.email.MockDetectiekenmerken
import io.pieterswillens.poc.testdoubles.email.MockEmailRepository
import io.pieterswillens.poc.testdoubles.email.StubbedEmailParser
import io.pieterswillens.poc.use.cases.dataset.CreeerDatasetUseCase
import io.pieterswillens.poc.use.cases.dataset.SlaSampleOpUseCase
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.email.RegistreerEmailUseCase
import io.pieterswillens.poc.use.cases.event.handlers.DatasetEventHandler
import io.pieterswillens.poc.use.cases.event.handlers.EmailEventHandler

class BlackboxApplicationContext: ApplicationContext {

    private val eventPublisher = MockEventPublisher()

    /* --- REPOSITORIES --- */
    override val emailRepository: MockEmailRepository = MockEmailRepository()
    override val sampleRepository = MockSampleRepository()
    override val datasetRepository = MockDatasetRepository()

    /* --- SERVICES --- */
    private val emailParser = StubbedEmailParser()
    private val geefSamplesService = sampleRepository

    /* --- REGISTRIES --- */
    private val detectiekenmerken = MockDetectiekenmerken()

    /* --- USE CASES --- */
    override val registreerEmail: RegistreerEmail = RegistreerEmailUseCase(emailParser, emailRepository, emailRepository, eventPublisher)
    private val berekenDetectiekenmerkenUseCase = BerekenDetectiekenmerkenUseCase(detectiekenmerken, emailRepository, eventPublisher)
    private val slaSampleOpUseCase = SlaSampleOpUseCase(sampleRepository, eventPublisher)
    override val creeerDataset: CreeerDataset = CreeerDatasetUseCase(geefSamplesService, datasetRepository, eventPublisher)

    init {
        eventPublisher.subscribe(EmailEventHandler(berekenDetectiekenmerkenUseCase))
        eventPublisher.subscribe(DatasetEventHandler(slaSampleOpUseCase))
    }

    fun reset() {
        emailRepository.clear()
        sampleRepository.clear()
        datasetRepository.clear()
    }

    companion object {
        val context = BlackboxApplicationContext()
    }
}
