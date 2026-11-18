package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.vocabulary.EmailId

interface BerekenDetectiekenmerkenDomainPresenter {
    fun detectieKenmerkenSuccesvolBerekend(emailId: EmailId)
}