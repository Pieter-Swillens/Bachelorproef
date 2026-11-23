package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.ports.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.email.ports.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres

class ReplyToWijktAfVanReturnPath(private val registreerbaarDomeinService: RegistreerbaarDomeinService) : Detectiekenmerk {
    override val id = DetectiekenmerkId.A3_REPLY_TO_AF_VAN_RETURN_PATH

    override fun evaluate(email: Email): ExtractieResult {
        val replyToDomein = email.replyToEmail.toRegistreerbaarDomein() ?: return ExtractieResult.NietBepaalbaar
        val returnPathDomein = email.returnPath.toRegistreerbaarDomein() ?: return ExtractieResult.NietBepaalbaar

        return if (replyToDomein == returnPathDomein) {
            ExtractieResult.NietAfwijkend
        } else {
            ExtractieResult.Afwijkend("Het adres van de ontvanger van een antwoord (${email.replyToEmail}) komt niet overeen met de ontvanger van de foutmelding indien het de email niet afgeleverd kan worden (${email.returnPath}).")
        }
    }

    private fun EmailAdres.toRegistreerbaarDomein() =
        when (val response = registreerbaarDomeinService.bepaalRegistreerbaarDomein(this.domeinnaam)) {
            is BepaalRegistreerbaarDomeinResponse.Ok -> response.registreerbaarDomein
            BepaalRegistreerbaarDomeinResponse.Ongeldig -> null
        }

}