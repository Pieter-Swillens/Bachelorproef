package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.ports.BepaalRegistreerbaarDomeinResponse
import io.pieterswillens.poc.domain.email.ports.RegistreerbaarDomeinService
import io.pieterswillens.poc.vocabulary.EmailAdres

class FromWijktAfVanReturnPath(private val registreerbaarDomeinService: RegistreerbaarDomeinService): Detectiekenmerk {
    override val id = DetectiekenmerkId.A2_FROM_WIJKT_AF_VAN_RETURN_PATH

    override fun evaluate(email: Email): ExtractieResult {
        val fromDomein = email.fromEmail.toRegistreerbaarDomein() ?: return ExtractieResult.NietBepaalbaar
        val returnPathDomein = email.returnPath.toRegistreerbaarDomein() ?: return ExtractieResult.NietBepaalbaar

        return if (fromDomein == returnPathDomein) {
            ExtractieResult.NietAfwijkend
        } else {
            ExtractieResult.Afwijkend("De afzender van de email (${email.fromEmail}) wordt niet op de hoogte gebracht van de email die niet kon worden afgeleverd. Deze melding wordt verstuurd naar ${email.returnPath}")
        }
    }

    private fun EmailAdres.toRegistreerbaarDomein() = when (val response = registreerbaarDomeinService.bepaalRegistreerbaarDomein(this.domeinnaam)) {
        is BepaalRegistreerbaarDomeinResponse.Ok -> response.registreerbaarDomein
        BepaalRegistreerbaarDomeinResponse.Ongeldig -> null
    }

}