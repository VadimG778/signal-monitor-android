package com.vadimg778.signalmonitor.app.di

import com.vadimg778.signalmonitor.feature.monitor.data.di.signalMonitorDataModule
import com.vadimg778.signalmonitor.feature.monitor.presentation.di.signalMonitorPresentationModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val platformModule = module {
    singleOf(::provideBackgroundDispatcher)
    singleOf(::provideApplicationScope)
}

val applicationModule = module {
    includes(platformModule, signalMonitorDataModule, signalMonitorPresentationModule)
}

@Suppress("InjectDispatcher")
private fun provideBackgroundDispatcher(): CoroutineDispatcher = Dispatchers.Default

private fun provideApplicationScope(backgroundDispatcher: CoroutineDispatcher): CoroutineScope =
    CoroutineScope(
        SupervisorJob() + backgroundDispatcher,
    )
