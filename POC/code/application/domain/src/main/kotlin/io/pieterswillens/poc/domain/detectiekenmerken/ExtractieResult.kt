package io.pieterswillens.poc.domain.detectiekenmerken

sealed interface ExtractieResult {
    data object NietAfwijkend: ExtractieResult
    data class Afwijkend(val bewijs: String): ExtractieResult
    data object NietBepaalbaar: ExtractieResult
}