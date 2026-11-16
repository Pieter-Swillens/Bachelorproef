package io.pieterswillens.poc.domain

// TODO: dead code for the moment, but needed for the event publisher
fun interface EventHandler {
    fun handle(event: Event)
}