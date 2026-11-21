package io.pieterswillens.poc.use.cases.event.handlers

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.email.events.EmailGeregistreerd
import io.pieterswillens.poc.use.cases.dataset.SlaSampleOpCommand
import io.pieterswillens.poc.use.cases.dataset.SlaSampleOpUseCase
import io.pieterswillens.poc.use.cases.event.handlers.presenters.SlaSampleOpLoggingPresenter

class DatasetEventHandler(
    private val slaSampleOpUseCase: SlaSampleOpUseCase
): EventHandler {
    override fun handle(event: Event) {
        when (event) {
            is EmailGeregistreerd -> slaSampleOpUseCase.execute(
                command = SlaSampleOpCommand(event.emailId, event.aard),
                presenter = SlaSampleOpLoggingPresenter
            )
        }
    }
}