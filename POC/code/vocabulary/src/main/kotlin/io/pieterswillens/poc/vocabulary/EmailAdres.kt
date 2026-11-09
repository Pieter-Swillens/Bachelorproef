package io.pieterswillens.poc.vocabulary

data class EmailAdres private constructor(val value: String) {
    val lokaalDeel = value.substringBeforeLast('@')
    val domeinnaam = value.substringAfterLast('@')

    override fun toString() = value

    companion object {
        fun from(input: String): EmailAdres {
            val scheiding = input.lastIndexOf('@')

            require(scheiding >= 0) { "Een mailinput bevat een apenstaartje: $input" }
            require(input.substring(0, scheiding).isNotBlank()) { "Het lokale deel mag niet leeg zijn: $input" }
            require(input.substring(scheiding + 1).isNotBlank()) { "De domeinnaam mag niet leeg zijn: $input" }

            return EmailAdres(input)
        }
    }
}