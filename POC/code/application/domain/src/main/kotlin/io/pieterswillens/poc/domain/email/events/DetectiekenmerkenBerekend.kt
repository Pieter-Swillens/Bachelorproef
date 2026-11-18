package io.pieterswillens.poc.domain.email.events

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.vocabulary.EmailId

data class DetectiekenmerkenBerekend(val emailId: EmailId): Event