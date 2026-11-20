package io.pieterswillens.poc.domain.email

import io.pieterswillens.poc.domain.Repository
import io.pieterswillens.poc.vocabulary.EmailId

interface EmailRepository: Repository<EmailId, Email>