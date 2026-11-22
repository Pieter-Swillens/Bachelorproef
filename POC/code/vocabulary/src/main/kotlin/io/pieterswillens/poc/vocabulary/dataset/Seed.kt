package io.pieterswillens.poc.vocabulary.dataset

data class Seed(val value: Int) {
    companion object {
        val DEFAULT = Seed(11)
    }
}
