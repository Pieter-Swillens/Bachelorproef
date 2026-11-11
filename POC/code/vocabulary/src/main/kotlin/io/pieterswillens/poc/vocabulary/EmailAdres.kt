package io.pieterswillens.poc.vocabulary


data class EmailAdres private constructor(val value: String) {
    val lokaalDeel = LokaalDeel(value.substringBeforeLast('@'))
    val domeinnaam = Domeinnaam(value.substringAfterLast('@'))

    data class LokaalDeel internal constructor(val value: String)
    data class Domeinnaam internal constructor(val value: String)

    override fun toString() = value

    companion object {
        val noOps = EmailAdres("noOps@pieterswillens.be")

        fun from(input: String): EmailAdres {
            val scheiding = input.lastIndexOf('@')

            require(scheiding >= 0) { "Een mailinput bevat een apenstaartje: $input" }
            require(input.substring(0, scheiding).isNotBlank()) { "Het lokale deel mag niet leeg zijn: $input" }
            require(input.substring(scheiding + 1).isNotBlank()) { "De domeinnaam mag niet leeg zijn: $input" }

            return EmailAdres(input)
        }
    }
}