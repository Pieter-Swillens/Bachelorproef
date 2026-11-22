package io.pieterswillens.poc.domain.dataset.presenter

import io.pieterswillens.poc.domain.Presenter

interface CreeerDatasetDomainPresenter: Presenter {
    fun datasetCreatieMisluktDoorTeKleineSampleset()
    fun datasetCreatieMisluktDoorTeWeinigVoorbeeldenVanBepaaldeEmailAard()
}