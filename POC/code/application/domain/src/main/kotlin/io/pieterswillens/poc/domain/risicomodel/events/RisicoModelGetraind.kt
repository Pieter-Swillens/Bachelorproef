package io.pieterswillens.poc.domain.risicomodel.events

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId

data class RisicoModelGetraind(val risicoModelId: RisicoModelId): Event