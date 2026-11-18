package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.vocabulary.EmailId

interface EmailRepository {
    fun save(email: Email)
    fun get(emailId : EmailId): Email?
}