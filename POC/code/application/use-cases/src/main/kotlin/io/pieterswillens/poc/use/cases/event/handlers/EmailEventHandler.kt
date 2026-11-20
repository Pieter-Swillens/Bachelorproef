package io.pieterswillens.poc.use.cases.event.handlers

import io.pieterswillens.poc.domain.Event
import io.pieterswillens.poc.domain.EventHandler
import io.pieterswillens.poc.domain.email.events.EmailGeregistreerd
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenCommand
import io.pieterswillens.poc.use.cases.email.BerekenDetectiekenmerkenUseCase
import io.pieterswillens.poc.use.cases.event.handlers.presenters.BerekenDetectiekenmerkenLoggingPresenter

class EmailEventHandler(
    private val berekenDetectiekenmerkenUseCase: BerekenDetectiekenmerkenUseCase
): EventHandler {
    override fun handle(event: Event) {
        when (event) {
            is EmailGeregistreerd -> berekenDetectiekenmerkenUseCase.execute(
                command = BerekenDetectiekenmerkenCommand(event.emailId),
                presenter = BerekenDetectiekenmerkenLoggingPresenter
            )
        }
    }
}