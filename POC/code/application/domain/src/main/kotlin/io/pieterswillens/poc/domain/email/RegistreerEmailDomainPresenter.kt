package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.vocabulary.EmailId

fun interface RegistreerEmailDomainPresenter {
    fun emailAlreadyRegistered(emailId: EmailId)
}