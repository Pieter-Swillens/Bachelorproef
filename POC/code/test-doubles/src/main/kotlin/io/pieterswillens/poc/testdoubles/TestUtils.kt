package io.pieterswillens.poc.testdoubles

object TestUtils {
    fun fail() {
        error("Should not be called")
    }
}