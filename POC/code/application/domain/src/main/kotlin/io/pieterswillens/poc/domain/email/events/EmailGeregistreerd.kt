package io.pieterswillens.poc.domain.email.events

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId

data class EmailGeregistreerd(val emailId: EmailId, val aard: EmailAard): Event