package io.pieterswillens.poc.api

import io.pieterswillens.poc.domain.email.presenter.RegistreerEmailDomainPresenter
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId

fun interface RegistreerEmail {
    fun registreerEmail(input: RegistreerEmailInput, presenter: RegistreerEmailPresenter)
}

data class RegistreerEmailInput(
    val bestandsnaam: String,
    val emailAard: EmailAard,
    val content: ByteArray
)

interface RegistreerEmailPresenter: RegistreerEmailDomainPresenter {
    fun emailRegistered(emailId: EmailId)
    fun emailRegistrationFailed()
}