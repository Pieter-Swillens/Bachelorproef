package io.pieterswillens.poc.jakarta.mail.adapter.config

import jakarta.mail.Session
import java.util.*

object JakartaMailParserConfig {
    fun session(): Session = Session.getInstance(Properties())
}