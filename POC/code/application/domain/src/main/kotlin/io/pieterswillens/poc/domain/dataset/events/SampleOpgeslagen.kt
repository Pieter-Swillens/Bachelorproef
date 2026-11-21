package io.pieterswillens.poc.domain.dataset.events

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.vocabulary.EmailId

data class SampleOpgeslagen(val emailId: EmailId): Event