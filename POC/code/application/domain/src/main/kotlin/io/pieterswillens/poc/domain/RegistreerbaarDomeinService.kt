package io.pieterswillens.poc.domain

import io.pieterswillens.poc.vocabulary.EmailAdres

interface RegistreerbaarDomeinService {
    fun bepaalRegistreerbaarDomein(emailAdres: EmailAdres): BepaalRegistreerbaarDomeinResponse
}

sealed interface BepaalRegistreerbaarDomeinResponse {
    data class Ok(val registreerbaarDomein: String): BepaalRegistreerbaarDomeinResponse
    data object Ongeldig: BepaalRegistreerbaarDomeinResponse
}
