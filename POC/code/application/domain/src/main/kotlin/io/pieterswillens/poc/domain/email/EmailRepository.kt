package io.pieterswillens.poc.domain.email

fun interface EmailRepository {
    fun save(email: Email)
}