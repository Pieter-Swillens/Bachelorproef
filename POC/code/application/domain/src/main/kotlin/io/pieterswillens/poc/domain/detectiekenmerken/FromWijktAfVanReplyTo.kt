package io.pieterswillens.poc.domain.detectiekenmerken

import io.pieterswillens.poc.domain.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.Email
import io.pieterswillens.poc.domain.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres

class FromWijktAfVanReplyTo(private val registreerbaarDomeinService: RegistreerbaarDomeinService) {
    fun evaluate(email: Email): ExtractieResult {
        val fromDomein = email.fromEmail.toRegistreerbaarDomein() ?: return ExtractieResult.NietBepaalbaar
        val replyToDomein = email.replyToEmail.toRegistreerbaarDomein() ?: return ExtractieResult.NietBepaalbaar

        return if (fromDomein == replyToDomein) {
            ExtractieResult.NietAfwijkend
        } else {
            ExtractieResult.Afwijkend("De afzender van de email (${email.fromEmail}) is niet de ontvanger van je antwoord. Je antwoord wordt verstuurd naar: ${email.replyToEmail}")
        }
    }

    private fun EmailAdres.toRegistreerbaarDomein() = when (val response = registreerbaarDomeinService.bepaalRegistreerbaarDomein(this.domeinnaam)) {
        is BepaalRegistreerbaarDomeinResponse.Ok -> response.registreerbaarDomein
        BepaalRegistreerbaarDomeinResponse.Ongeldig -> null
    }

}