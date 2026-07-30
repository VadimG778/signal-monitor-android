package com.vadimg778.signalmonitor.feature.monitor.presentation

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalMonitor
import kotlinx.collections.immutable.toPersistentList

object SignalMonitorUiMapper {

    fun map(monitor: SignalMonitor): SignalMonitorUiState = SignalMonitorUiState(
        generators = monitor.generators.map { monitoredGenerator ->
            val generator = monitoredGenerator.generator

            GeneratorItemUiModel(
                id = generator.id,
                name = generator.name,
                colorArgb = generator.color.argb,
                remainingTimeMillis = generator.remainingTimeMillis,
                currentValue = generator.currentValue,
                isVisible = monitoredGenerator.isVisible,
                isCompleted = generator.status == GeneratorStatus.COMPLETED,
            )
        }.toPersistentList(),
        series = monitor.generators.map { monitoredGenerator ->
            val generator = monitoredGenerator.generator

            SignalSeriesUiModel(
                id = generator.id,
                name = generator.name,
                colorArgb = generator.color.argb,
                isVisible = monitoredGenerator.isVisible,
                isActive = generator.status == GeneratorStatus.ACTIVE,
                points = generator.points,
            )
        }.toPersistentList(),
    )
}
