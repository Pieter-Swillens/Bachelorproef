package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.vocabulary.EmailAard
import io.pieterswillens.poc.vocabulary.EmailId

data class Sample(
    val emailId: EmailId,
    val bestandsnaam: String,
    val aard: EmailAard
)