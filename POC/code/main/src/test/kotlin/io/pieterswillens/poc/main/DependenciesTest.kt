package io.pieterswillens.poc.main

import io.pieterswillens.poc.acceptance.tests.DatasetTest
import io.pieterswillens.poc.acceptance.tests.EmailTest
import org.junit.jupiter.api.DisplayName

@SystemTest
@DisplayName("When testing for correct wiring")
class DependenciesTest: EmailTest, DatasetTest {
    override val context = ProductionApplicationContext()
    override val emailInput = javaClass.getResourceAsStream("/mail.eml")!!.readBytes()
}