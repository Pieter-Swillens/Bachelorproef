package io.pieterswillens.poc.cli.adapter.mocks

interface MockApplicationApi<TEST_ACTION> {
    fun reset()
    fun setNextAction(action: TEST_ACTION)
}