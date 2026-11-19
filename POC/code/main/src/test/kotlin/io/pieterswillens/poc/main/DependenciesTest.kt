package io.pieterswillens.poc.main

import io.pieterswillens.poc.acceptance.tests.EmailTest
import org.junit.jupiter.api.DisplayName

@SystemTest
@DisplayName("Given an email flow in production context")
class DependenciesTest: EmailTest {
    override val context = ProductionApplicationContext()
    override val emailInput = javaClass.getResourceAsStream("/mail.eml")!!.readBytes()
}