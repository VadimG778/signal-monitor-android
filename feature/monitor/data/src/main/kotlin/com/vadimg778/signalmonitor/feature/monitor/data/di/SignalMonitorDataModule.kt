package com.vadimg778.signalmonitor.feature.monitor.data.di

import com.vadimg778.signalmonitor.feature.monitor.data.factory.SignalGeneratorFactoryImpl
import com.vadimg778.signalmonitor.feature.monitor.data.repository.SignalGeneratorRepositoryImpl
import com.vadimg778.signalmonitor.feature.monitor.data.source.RandomWalkPointSourceFactoryImpl
import com.vadimg778.signalmonitor.feature.monitor.data.time.SystemTimeProviderImpl
import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.factory.SignalGeneratorFactory
import com.vadimg778.signalmonitor.feature.monitor.domain.repository.SignalGeneratorRepository
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSourceFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val signalMonitorDataModule = module {
    singleOf(::provideTimeProvider)
    singleOf(::provideSignalGeneratorFactory)
    singleOf(::provideSignalPointSourceFactory)
    singleOf(::provideSignalGeneratorRepository)
}

private fun provideTimeProvider(): TimeProvider = SystemTimeProviderImpl

private fun provideSignalGeneratorFactory(timeProvider: TimeProvider): SignalGeneratorFactory =
    SignalGeneratorFactoryImpl(timeProvider)

private fun provideSignalPointSourceFactory(timeProvider: TimeProvider): SignalPointSourceFactory =
    RandomWalkPointSourceFactoryImpl(timeProvider)

private fun provideSignalGeneratorRepository(
    applicationScope: CoroutineScope,
    generatorFactory: SignalGeneratorFactory,
    pointSourceFactory: SignalPointSourceFactory,
    timeProvider: TimeProvider,
): SignalGeneratorRepository = SignalGeneratorRepositoryImpl(
    applicationScope = applicationScope,
    generatorFactory = generatorFactory,
    pointSourceFactory = pointSourceFactory,
    timeProvider = timeProvider,
)
