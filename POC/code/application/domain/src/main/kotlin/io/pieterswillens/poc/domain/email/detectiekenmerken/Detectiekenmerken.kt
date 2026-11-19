package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email

interface Detectiekenmerken {
    fun geefKenmerkenProfiel(email: Email): Map<DetectiekenmerkId, ExtractieResult>
}

class DetectiekenmerkenRegistry(private val kenmerken: List<Detectiekenmerk>): Detectiekenmerken {
    override fun geefKenmerkenProfiel(email: Email) = kenmerken.associate { it.id to it.evaluate(email) }
}