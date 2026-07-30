package com.vadimg778.signalmonitor.app.di

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
class ApplicationModuleTest {

    @Test
    fun `dependency graph is complete`() {
        applicationModule.verify()
    }
}
