package com.vadimg778.signalmonitor.feature.monitor.domain.interactor

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.MonitoredSignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalMonitor
import com.vadimg778.signalmonitor.feature.monitor.domain.usecase.ObserveSortedGeneratorsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class SignalMonitorInteractor(observeSortedGenerators: ObserveSortedGeneratorsUseCase) {

    private val hiddenGeneratorIds = MutableStateFlow<Set<GeneratorId>>(emptySet())

    val monitor: Flow<SignalMonitor> = combine(
        observeSortedGenerators(),
        hiddenGeneratorIds,
    ) { generators, hiddenIds ->
        SignalMonitor(
            generators = generators.map { generator ->
                MonitoredSignalGenerator(
                    generator = generator,
                    isVisible = generator.id !in hiddenIds,
                )
            },
        )
    }

    fun setGeneratorVisibility(generatorId: GeneratorId, isVisible: Boolean) {
        hiddenGeneratorIds.update { hiddenIds ->
            if (isVisible) {
                hiddenIds - generatorId
            } else {
                hiddenIds + generatorId
            }
        }
    }
}
