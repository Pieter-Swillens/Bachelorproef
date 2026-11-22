package io.pieterswillens.poc.main

import io.pieterswillens.poc.acceptance.tests.ApplicationContext
import io.pieterswillens.poc.main.dependencies.Dependencies

class ProductionApplicationContext: ApplicationContext {
    override val emailRepository = Dependencies.emailRepository
    override val sampleRepository = Dependencies.sampleRepository
    override val datasetRepository = Dependencies.datasetRepository

    override val registreerEmail = Dependencies.registreerEmail
    override val creeerDataset = Dependencies.creeerDataset
}