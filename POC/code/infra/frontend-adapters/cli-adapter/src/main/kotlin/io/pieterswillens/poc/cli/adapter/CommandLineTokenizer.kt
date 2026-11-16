package io.pieterswillens.poc.cli.adapter

internal object CommandLineTokenizer {
    private val QUOTES = setOf('"', '\'')

    fun tokenize(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val token = StringBuilder()
        var openQuote: Char? = null
        var tokenStarted = false

        fun endToken() {
            if (tokenStarted) tokens += token.toString()
            token.clear()
            tokenStarted = false
        }

        for (char in line) {
            when {
                openQuote != null -> if (char == openQuote) openQuote = null else token.append(char)
                char in QUOTES -> { openQuote = char; tokenStarted = true }
                char.isWhitespace() -> endToken()
                else -> { token.append(char); tokenStarted = true }
            }
        }
        endToken()

        return tokens
    }

}