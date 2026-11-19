package io.pieterswillens.poc.domain.email.presenter

import io.pieterswillens.poc.domain.Presenter
import io.pieterswillens.poc.vocabulary.EmailId

fun interface RegistreerEmailDomainPresenter: Presenter {
    fun emailAlreadyRegistered(emailId: EmailId)
}