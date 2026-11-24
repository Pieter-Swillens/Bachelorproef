package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.ports.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.email.ports.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres

class AantalVerschillendeDomeinen(private val registreerbaarDomeinService: RegistreerbaarDomeinService): Detectiekenmerk {
    override val id: DetectiekenmerkId = DetectiekenmerkId.A4_AANTAL_VERSCHILLENDE_DOMEINEN

    override fun evaluate(email: Email): ExtractieResult {
        val result = setOf(
            email.fromEmail.toRegistreerbaarDomein(),
            email.replyToEmail.toRegistreerbaarDomein(),
            email.returnPath.toRegistreerbaarDomein(),
        )

        return if (result.any { it == null }) {
            ExtractieResult.NietBepaalbaar
        } else {
            ExtractieResult.Gemeten(result.size)
        }
    }

    private fun EmailAdres.toRegistreerbaarDomein() = when (val response = registreerbaarDomeinService.bepaalRegistreerbaarDomein(this.domeinnaam)) {
        is BepaalRegistreerbaarDomeinResponse.Ok -> response.registreerbaarDomein
        BepaalRegistreerbaarDomeinResponse.Ongeldig -> null
    }
}
