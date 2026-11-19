package io.pieterswillens.poc.domain.email.detectiekenmerken

import io.pieterswillens.poc.domain.email.Email

interface Detectiekenmerk {
    val id: DetectiekenmerkId

    fun evaluate(email: Email): ExtractieResult
}

enum class DetectiekenmerkId {
    A1_FROM_WIJKT_AF_VAN_REPLY_TO
}