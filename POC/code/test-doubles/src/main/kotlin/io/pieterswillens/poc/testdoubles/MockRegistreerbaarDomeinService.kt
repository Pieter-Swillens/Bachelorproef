package io.pieterswillens.poc.testdoubles

import io.pieterswillens.poc.domain.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres

class MockRegistreerbaarDomeinService : RegistreerbaarDomeinService {

    override fun bepaalRegistreerbaarDomein(domeinnaam: EmailAdres.Domeinnaam): BepaalRegistreerbaarDomeinResponse {
        val registreerbaarDomeinParts = domeinnaam.value
            .lowercase()
            .split(".")

        return if (registreerbaarDomeinParts.size < 2) {
            BepaalRegistreerbaarDomeinResponse.Ongeldig
        } else {
            BepaalRegistreerbaarDomeinResponse.Ok(
                registreerbaarDomeinParts
                    .takeLast(2)
                    .joinToString(".")
            )
        }
    }

}