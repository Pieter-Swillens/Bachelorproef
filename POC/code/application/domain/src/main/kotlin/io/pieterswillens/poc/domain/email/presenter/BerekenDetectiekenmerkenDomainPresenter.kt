package io.pieterswillens.poc.domain.email.presenter

import io.pieterswillens.poc.vocabulary.EmailId

interface BerekenDetectiekenmerkenDomainPresenter {
    fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId)
}