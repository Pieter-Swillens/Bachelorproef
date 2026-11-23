package io.pieterswillens.poc.main

import io.pieterswillens.poc.acceptance.tests.DatasetTest
import io.pieterswillens.poc.acceptance.tests.EmailTest
import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkId
import io.pieterswillens.poc.main.dependencies.Dependencies
import io.pieterswillens.poc.testdoubles.email.testbuilders.GeregistreerdeEmailTestBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@SystemTest
@DisplayName("When testing for correct wiring")
class DependenciesTest: EmailTest, DatasetTest {
    override val context = ProductionApplicationContext()
    override val emailInput = javaClass.getResourceAsStream("/mail.eml")!!.readBytes()

    @Test
    fun `then DetectiekenmerkenRegistry contains all known Detectiekenmerken`() {
        val anEmail = GeregistreerdeEmailTestBuilder().build()

        val kenmerkenProfiel = Dependencies.detectiekenmerkenRegistry.geefKenmerkenProfiel(anEmail)

        assertThat(kenmerkenProfiel.keys).containsExactlyInAnyOrder(*DetectiekenmerkId.entries.toTypedArray())
    }
}