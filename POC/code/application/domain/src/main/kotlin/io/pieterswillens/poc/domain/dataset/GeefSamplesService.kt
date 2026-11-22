package io.pieterswillens.poc.domain.dataset

fun interface GeefSamplesService {
    fun geefSamples(): Set<Sample>
}
