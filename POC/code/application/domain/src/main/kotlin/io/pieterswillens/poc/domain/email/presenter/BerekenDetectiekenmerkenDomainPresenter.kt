package io.pieterswillens.poc.domain.email.presenter

import io.pieterswillens.poc.domain.Presenter
import io.pieterswillens.poc.vocabulary.EmailId

interface BerekenDetectiekenmerkenDomainPresenter: Presenter {
    fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId)
}