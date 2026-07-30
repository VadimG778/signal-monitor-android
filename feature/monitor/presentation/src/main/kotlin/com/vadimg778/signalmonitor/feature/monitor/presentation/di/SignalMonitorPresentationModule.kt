package com.vadimg778.signalmonitor.feature.monitor.presentation.di

import com.vadimg778.signalmonitor.feature.monitor.domain.interactor.SignalMonitorInteractor
import com.vadimg778.signalmonitor.feature.monitor.domain.usecase.ObserveSortedGeneratorsUseCase
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalMonitorViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val signalMonitorPresentationModule = module {
    factoryOf(::ObserveSortedGeneratorsUseCase)
    factoryOf(::SignalMonitorInteractor)
    viewModelOf(::SignalMonitorViewModel)
}
