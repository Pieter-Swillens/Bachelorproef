package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.domain.email.detectiekenmerken.ExtractieResult

interface EmailStatus {
    data object Geregistreerd: EmailStatus
    data class DetectiekenmerkenBerekend(val kenmerkenprofiel: Map<String, ExtractieResult>): EmailStatus
}