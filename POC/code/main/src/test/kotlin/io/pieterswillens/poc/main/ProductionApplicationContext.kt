package io.pieterswillens.poc.main

import io.pieterswillens.poc.acceptance.tests.ApplicationContext
import io.pieterswillens.poc.main.dependencies.Dependencies

class ProductionApplicationContext: ApplicationContext {
    override val registreerEmail = Dependencies.registreerEmail
    override val emailRepository = Dependencies.emailRepository
}