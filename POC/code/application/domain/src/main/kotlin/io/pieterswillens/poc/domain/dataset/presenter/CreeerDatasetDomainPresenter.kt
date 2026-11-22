package io.pieterswillens.poc.domain.dataset.presenter

import io.pieterswillens.poc.vocabulary.Presenter

interface CreeerDatasetDomainPresenter: Presenter {
    fun datasetCreatieMisluktDoorTeKleineSampleset()
    fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard()
}