package io.pieterswillens.poc.main.dependencies

import io.pieterswillens.poc.jakarta.mail.adapter.JakartaMailParser
import io.pieterswillens.poc.jakarta.mail.adapter.config.JakartaMailParserConfig
import io.pieterswillens.poc.testdoubles.MockEmailRepository
import io.pieterswillens.poc.use.cases.RegistreerEmailUseCase

internal object Dependencies {
    private val emailParser = JakartaMailParser(JakartaMailParserConfig)
    private val emailRepository = MockEmailRepository()

    val registreerEmail = RegistreerEmailUseCase(
        emailParser = emailParser,
        emailUniquenessValidator = emailRepository,
        emailRepository = emailRepository
    )
}