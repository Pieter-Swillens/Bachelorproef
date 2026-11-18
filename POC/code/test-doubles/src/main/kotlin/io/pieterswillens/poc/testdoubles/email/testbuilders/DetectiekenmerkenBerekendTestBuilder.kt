package io.pieterswillens.poc.testdoubles.email.testbuilders

import io.pieterswillens.poc.domain.email.Email
import io.pieterswillens.poc.domain.email.EmailSnapshot
import io.pieterswillens.poc.domain.email.EmailStatus
import io.pieterswillens.poc.domain.email.detectiekenmerken.ExtractieResult

class DetectiekenmerkenBerekendTestBuilder internal constructor(
    private val snapshot: EmailSnapshot,
    private val kenmerkenprofiel: Map<String, ExtractieResult> = DEFAULT_KENMERKENPROFEEL
) {

    fun with(
        kenmerkenprofiel: Map<String, ExtractieResult>? = null
    ) = DetectiekenmerkenBerekendTestBuilder(
        snapshot = snapshot,
        kenmerkenprofiel = kenmerkenprofiel ?: this.kenmerkenprofiel
    )

    fun build() = Email.fromSnapshot(
        snapshot = snapshot.copy(status = EmailStatus.DetectiekenmerkenBerekend(kenmerkenprofiel)),
    )

    companion object {
        private val DEFAULT_KENMERKENPROFEEL = mapOf("A1" to ExtractieResult.NietAfwijkend)
    }
}
