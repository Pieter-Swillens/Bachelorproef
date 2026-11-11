package io.pieterswillens.poc.domain

import io.pieterswillens.poc.vocabulary.EmailId

fun interface RegistreerEmailDomainPresenter {
    fun emailAlreadyRegistered(emailId: EmailId)
}