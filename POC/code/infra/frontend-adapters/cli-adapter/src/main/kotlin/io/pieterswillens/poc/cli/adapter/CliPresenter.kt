package io.pieterswillens.poc.cli.adapter

abstract class CliPresenter {
    protected var exitCode: Int? = null

    fun getResponse(): Int {
        return exitCode ?: error("Unexpected error occured")
    }
}