package io.pieterswillens.poc.acceptance.tests

import io.pieterswillens.poc.acceptance.tests.config.BlackboxApplicationContext
import org.junit.jupiter.api.DisplayName

@DisplayName("Given an application api to creeer a dataset")
class BlackboxDatasetTest: DatasetTest {
    override val context = BlackboxApplicationContext.context
}