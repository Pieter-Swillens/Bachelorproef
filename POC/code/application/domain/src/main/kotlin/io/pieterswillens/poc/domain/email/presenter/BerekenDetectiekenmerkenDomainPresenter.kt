package io.pieterswillens.poc.domain.email.presenter

import io.pieterswillens.poc.vocabulary.EmailId
import io.pieterswillens.poc.vocabulary.Presenter

interface BerekenDetectiekenmerkenDomainPresenter: Presenter {
    fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId)
}