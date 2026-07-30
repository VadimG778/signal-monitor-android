package com.vadimg778.signalmonitor.feature.monitor.domain.usecase

import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.repository.SignalGeneratorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSortedGeneratorsUseCase(private val repository: SignalGeneratorRepository) {

    operator fun invoke(): Flow<List<SignalGenerator>> = repository.observeGenerators()
        .map { generators ->
            generators.sortedWith(
                compareBy<SignalGenerator> { it.remainingTimeMillis }
                    .thenBy { it.id.value },
            )
        }
}
