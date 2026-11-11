package io.pieterswillens.poc.acceptance.tests.config

import org.junit.jupiter.api.AfterEach

interface AcceptanceTest {
    val context: BlackboxApplicationContext
        get() = BlackboxApplicationContext.context

    @AfterEach
    fun tearDown() {
        context.reset()
    }
}