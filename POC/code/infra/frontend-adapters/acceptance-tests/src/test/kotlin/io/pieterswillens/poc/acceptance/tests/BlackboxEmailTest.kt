package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.acceptance.tests.config.AcceptanceTest
import io.pieterswillens.poc.acceptance.tests.config.BlackboxApplicationContext
import io.pieterswillens.poc.testdoubles.email.testbuilders.RawEmailTestBuilder
import org.junit.jupiter.api.DisplayName

@DisplayName("Given an application api to registreer an email")
class BlackboxEmailTest: AcceptanceTest, EmailTest {
    override val context = BlackboxApplicationContext.context
    override val emailInput = RawEmailTestBuilder().build().content
}
