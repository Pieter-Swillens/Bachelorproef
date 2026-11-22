package io.pieterswillens.poc.use.cases

import io.pieterswillens.poc.vocabulary.Presenter

interface UseCase<COMMAND: Command, PRESENTER: Presenter> {
    fun execute(command: COMMAND, presenter: PRESENTER)
}

interface Command
