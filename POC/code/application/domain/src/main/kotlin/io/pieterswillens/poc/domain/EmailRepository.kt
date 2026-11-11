package io.pieterswillens.poc.domain

fun interface EmailRepository {
    fun save(email: Email)
}