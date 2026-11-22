package io.pieterswillens.poc.api

import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.Presenter

fun interface RegistreerEmail {
    fun registreerEmail(input: RegistreerEmailInput, presenter: RegistreerEmailPresenter)
}

data class RegistreerEmailInput(
    val bestandsnaam: String,
    val emailAard: EmailAard,
    val content: ByteArray
)

interface RegistreerEmailPresenter: Presenter {
    fun emailRegistered(emailId: EmailId)
    fun emailRegistrationFailed()
    fun emailAlreadyRegistered(emailId: EmailId)
}