package io.pieterswillens.poc.domain.risicomodel

import io.pieterswillens.poc.domain.Repository
import io.pieterswillens.poc.vocabulary.risicomodel.RisicoModelId

interface RisicoModelRepository: Repository<RisicoModelId, RisicoModel>