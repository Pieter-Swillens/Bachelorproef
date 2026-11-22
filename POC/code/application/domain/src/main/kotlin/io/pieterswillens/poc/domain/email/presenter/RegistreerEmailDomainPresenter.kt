package io.pieterswillens.poc.domain.email.presenter

import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.Presenter

fun interface RegistreerEmailDomainPresenter: Presenter {
    fun emailAlreadyRegistered(emailId: EmailId)
}