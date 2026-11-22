package io.pieterswillens.poc.cli.adapter.creeer.dataset

import io.pieterswillens.poc.api.CreeerDatasetPresenter
import io.pieterswillens.poc.cli.adapter.CliPresenter
import io.pieterswillens.poc.cli.adapter.ExitCode
import io.pieterswillens.poc.vocabulary.dataset.DatasetId
import io.pieterswillens.poc.vocabulary.dataset.Seed
import org.slf4j.LoggerFactory
import java.io.PrintWriter

class CreeerDatasetCliPresenter(
    private val out: PrintWriter,
    private val err: PrintWriter,
) : CreeerDatasetPresenter, CliPresenter() {
    override fun datasetGecreeerd(datasetId: DatasetId, seed: Seed) {
        LOGGER.info("Dataset met ID $datasetId successvol gecreëerd.")
        out.println("Dataset successvol gecreëerd.")
        exitCode = ExitCode.OK
    }

    override fun datasetCreatieMisluktDoorTeKleineSampleset() {
        LOGGER.info("Dataset kon niet worden gecreëerd: er moeten minstens 10 geregistreerde e-mails zijn.")
        err.println("Dataset kon niet worden gecreëerd: er moeten minstens 10 geregistreerde e-mails zijn.")
        exitCode = ExitCode.MISLUKT
    }

    override fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard() {
        LOGGER.info("Dataset kon niet worden gecreëerd: moeten minstens 2 e-mails van elke klasse (phishing of legitiem) geregistreerd zijn.")
        err.println("Dataset kon niet worden gecreëerd: moeten minstens 2 e-mails van elke klasse (phishing of legitiem) geregistreerd zijn.")
        exitCode = ExitCode.MISLUKT
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreeerDatasetCliPresenter::class.java)
    }

}