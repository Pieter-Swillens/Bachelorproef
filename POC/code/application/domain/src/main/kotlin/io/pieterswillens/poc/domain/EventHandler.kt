package io.pieterswillens.poc.domain

fun interface EventHandler {
    fun handle(event: Event)
}