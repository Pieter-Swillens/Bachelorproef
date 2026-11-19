package io.pieterswillens.poc.testdoubles.email

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.detectiekenmerken.DetectiekenmerkId
import io.pieterswillens.poc.domain.email.detectiekenmerken.Detectiekenmerken
import io.pieterswillens.poc.domain.email.detectiekenmerken.ExtractieResult

class MockDetectiekenmerken : Detectiekenmerken {
    var kenmerkenProfiel: Map<DetectiekenmerkId, ExtractieResult> = DEFAULT

    override fun geefKenmerkenProfiel(email: Email) = kenmerkenProfiel

    companion object {
        private val DEFAULT = mapOf(DetectiekenmerkId.A1_FROM_WIJKT_AF_VAN_REPLY_TO to ExtractieResult.NietAfwijkend)
    }
}