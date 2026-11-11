package io.pieterswillens.poc.scripts

import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import java.util.*

class EmailParsingScript {

    fun parseMail(resource: String) {
        val email = javaClass.getResourceAsStream(resource)

        val session = Session.getDefaultInstance(Properties(), null)
        val message = MimeMessage(session, email)

        println("MessageId: ${message.messageID}")
        println("From header: ${message.from.joinToString(", ") { it.toString() }}")
        println("Reply to header: ${message.replyTo?.joinToString(", ") { it.toString() }}")

        println("Body: ${message.content}")
    }

}

fun main() {
    val txtFilePath = "/mail.txt"
    val emlFilePath = "/mail.eml"

    EmailParsingScript().parseMail(txtFilePath)
//    EmailParsingScript().parseMail(emlFilePath)
}