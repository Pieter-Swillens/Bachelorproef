package io.pieterswillens.poc.domain.detectiekenmerken

import io.pieterswillens.poc.domain.Email

object FromWijktAfVanReplyTo {
    fun evaluate(eenEmail: Email): ExtractieResult {
        return when {
            eenEmail.fromHeader == null -> ExtractieResult.NietBepaalbaar
            eenEmail.replyToHeader == null -> ExtractieResult.NietBepaalbaar
            eenEmail.fromHeader == eenEmail.replyToHeader -> ExtractieResult.NietAfwijkend
            eenEmail.fromHeader != eenEmail.replyToHeader -> ExtractieResult.Afwijkend("De afzender van de email (${eenEmail.fromHeader}) is niet de ontvanger van je antwoord. Je antwoord wordt verstuurd naar: ${eenEmail.replyToHeader}.")
            else -> error("Unexpected error")
        }
    }

}