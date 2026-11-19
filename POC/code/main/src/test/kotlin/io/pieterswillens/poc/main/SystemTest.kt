package io.pieterswillens.poc.main

import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("system-test")
annotation class SystemTest