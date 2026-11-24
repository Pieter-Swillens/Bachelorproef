package io.pieterswillens.poc.domain.email.detectiekenmerken

sealed interface ExtractieResult {
    data object NietAfwijkend: ExtractieResult
    data class Afwijkend(val bewijs: String): ExtractieResult
    data class Gemeten(val waarde: Int): ExtractieResult
    data object NietBepaalbaar: ExtractieResult
}