package io.pieterswillens.poc.domain.email.presenter

import io.pieterswillens.poc.vocabulary.EmailId

fun interface RegistreerEmailDomainPresenter {
    fun emailAlreadyRegistered(emailId: EmailId)
}