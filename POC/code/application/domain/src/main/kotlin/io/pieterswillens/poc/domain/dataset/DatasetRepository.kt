package io.pieterswillens.poc.domain.dataset

import io.pieterswillens.poc.domain.Repository
import io.pieterswillens.poc.vocabulary.dataset.DatasetId

interface DatasetRepository: Repository<DatasetId, Dataset>