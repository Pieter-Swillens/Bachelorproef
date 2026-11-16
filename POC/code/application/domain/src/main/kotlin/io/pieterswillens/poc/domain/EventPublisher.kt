package io.pieterswillens.poc.domain

fun interface EventPublisher {
    fun publish(event: Event)
}