package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.domain.Repository
import io.pieterswillens.poc.vocabulary.EmailId

interface SampleRepository: Repository<EmailId, Sample>